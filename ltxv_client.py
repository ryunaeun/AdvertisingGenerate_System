import json
import websocket
import requests
import uuid
import os
import glob
import time
from config import load_config
from typing import Dict, Any, Optional, Set
from PIL import Image

class LTXVClient:
    # 클래스 레벨 상수 정의
    DEFAULT_FOLDER_NAME = "LTXVideo"
    DEFAULT_BASE_FILENAME = "video"
    DEFAULT_FRAME_LENGTH = 65
    DEFAULT_MAX_SIZE = 800  # JSON에서는 800x800을 사용
    DEFAULT_FPS = 25  # JSON에서는 25fps를 사용
    DEFAULT_WIDTH = 800
    DEFAULT_HEIGHT = 800
    
    def __init__(self, server_url: str = None, 
                 base_output_dir: str = r"D:\ComfyUI_windows_portable\ComfyUI\output"):
        if server_url is None:
            config = load_config()
            server_url = f"http://{config['IP']}:{config['PORT']}"
        self.server_url = server_url
        self.base_output_dir = base_output_dir
        self.client_id = str(uuid.uuid4())
        self.ws = None

    def _connect_websocket(self):
        ws_url = f"ws://{self.server_url.split('//')[1]}/ws?clientId={self.client_id}"
        self.ws = websocket.WebSocket()
        self.ws.connect(ws_url)

    def _get_existing_files(self, folder_name: str = DEFAULT_FOLDER_NAME) -> Set[str]:
        folder_path = os.path.join(self.base_output_dir, folder_name)
        if not os.path.exists(folder_path):
            os.makedirs(folder_path, exist_ok=True)
        pattern = os.path.join(folder_path, "*.mp4")
        return set(os.path.basename(f) for f in glob.glob(pattern))

    def _wait_for_new_file(self, folder_name: str = DEFAULT_FOLDER_NAME, 
                          existing_files: Set[str] = None, timeout: int = 180) -> str:
        if existing_files is None:
            existing_files = set()
        start_time = time.time()
        folder_path = os.path.join(self.base_output_dir, folder_name)
        
        while time.time() - start_time < timeout:
            current_files = self._get_existing_files(folder_name)
            new_files = current_files - existing_files
            if new_files:
                newest_file = max(new_files, key=lambda x: os.path.getctime(os.path.join(folder_path, x)))
                return os.path.join(folder_path, newest_file)
            time.sleep(0.5)
        raise TimeoutError("New video file was not detected within the timeout period")

    def _calculate_dimensions(self, image_path: str, max_size: int = DEFAULT_MAX_SIZE) -> tuple[int, int]:
        with Image.open(image_path) as img:
            width, height = img.size
            
            if width >= height:
                new_width = max_size
                new_height = int((height * max_size) / width)
            else:
                new_height = max_size
                new_width = int((width * max_size) / height)
                
            new_width = (new_width // 32) * 32
            new_height = (new_height // 32) * 32
            
            return new_width, new_height

    async def _upload_image(self, image_path: str) -> str:
        if not os.path.isfile(image_path):
            raise ValueError(f"Image file not found: {image_path}")
            
        with open(image_path, 'rb') as file:
            filename = os.path.basename(image_path)
            files = {
                'image': (filename, file, 'image/png')
            }
            upload_url = f"{self.server_url}/upload/image"
            response = requests.post(upload_url, files=files)
            
            if response.status_code != 200:
                raise Exception(f"Failed to upload image: {response.text}")
                
            response_data = response.json()
            if not response_data.get('name'):
                raise Exception("Failed to get uploaded image name from response")
                
            return response_data['name']

    def _create_workflow(self, prompt: str, uploaded_image: str, 
                        folder_name: str = DEFAULT_FOLDER_NAME,
                        base_filename: str = DEFAULT_BASE_FILENAME, 
                        seed: Optional[int] = None,
                        frame_length: int = DEFAULT_FRAME_LENGTH, 
                        width: int = DEFAULT_WIDTH, 
                        height: int = DEFAULT_HEIGHT,
                        fps: int = DEFAULT_FPS) -> Dict[str, Any]:
        if seed is None:
            seed = int(time.time() * 1000) % (2**32)

        workflow = {
            "106": {
                "inputs": {
                    "image": uploaded_image,
                    "upload": "image"
                },
                "class_type": "LoadImage"
            },
            "155": {
                "inputs": {
                    "image": ["106", 0],
                    "width": width,
                    "height": height,
                    "upscale_method": "bilinear",
                    "keep_proportion": True,
                    "divisible_by": 32,
                    "crop": "disabled"
                },
                "class_type": "ImageResizeKJ"
            },
            "110": {
                "inputs": {
                    "model": "microsoft/Florence-2-base",
                    "precision": "fp16",
                    "attention": "sdpa"
                },
                "class_type": "DownloadAndLoadFlorence2Model"
            },
            "111": {
                "inputs": {
                    "text_input": "",
                    "task": "more_detailed_caption",
                    "fill_mask": True,
                    "keep_model_loaded": False,
                    "max_new_tokens": 256,
                    "num_beams": 3,
                    "do_sample": True,
                    "output_mask_select": "",
                    "seed": 285091998991985,
                    "image": ["106", 0],
                    "florence2_model": ["110", 0]
                },
                "class_type": "Florence2Run"
            },
            "112": {
                "inputs": {
                    "string": ["111", 2],
                    "old": "image",
                    "new": "video"
                },
                "class_type": "String Replace (mtb)"
            },
            "117": {
                "inputs": {
                    "string": ["112", 0],
                    "old": "photo",
                    "new": "video"
                },
                "class_type": "String Replace (mtb)"
            },
            "113": {
                "inputs": {
                    "string": ["117", 0],
                    "old": "painting",
                    "new": "video"
                },
                "class_type": "String Replace (mtb)"
            },
            "114": {
                "inputs": {
                    "string": ["113", 0],
                    "old": "illustration",
                    "new": "video"
                },
                "class_type": "String Replace (mtb)"
            },
            "115": {
                "inputs": {
                    "action": "append",
                    "tidy_tags": "no",
                    "text_a": prompt,
                    "text_b": "",
                    "text_c": ["114", 0],
                    "result": prompt  # This will be replaced with actual append result
                },
                "class_type": "StringFunction|pysssss"
            },
            "102": {
                "inputs": {
                    "ckpt_name": "ltx-video-2b-v0.9.1.safetensors",
                    "dtype": "bfloat16"
                },
                "class_type": "LTXVLoader"
            },
            "142": {
                "inputs": {
                    "clip_name": "t5xxl_fp8.safetensors",
                    "type": "ltxv"
                },
                "class_type": "CLIPLoader"
            },
            "128": {
                "inputs": {
                    "model": ["102", 0],
                    "stg_mode": "attention",
                    "block_indices": "14"
                },
                "class_type": "LTXVApplySTG"
            },
            "103": {
                "inputs": {
                    "model": ["128", 0],
                    "vae": ["102", 1],
                    "conditioning": ["155", 0],
                    "preset": "Custom",
                    "width": width,
                    "height": height,
                    "frames_number": frame_length,
                    "frame_rate": fps,
                    "batch": 1,
                    "img_compression": 29,
                    "mixed_precision": True
                },
                "class_type": "LTXVModelConfigurator"
            },
            "87": {
                "inputs": {
                    "text": ["115", 0],  # Changed to use processed text from string nodes
                    "clip": ["142", 0]
                },
                "class_type": "CLIPTextEncode"
            },
            "88": {
                "inputs": {
                    "text": "worst quality, inconsistent motion, blurry, jittery, distorted, watermarks, conversation, speaking, talking",
                    "clip": ["142", 0]
                },
                "class_type": "CLIPTextEncode"
            },
            "81": {
                "inputs": {
                    "model": ["103", 0],
                    "scheduler": "normal",
                    "steps": 25,
                    "denoise": 1
                },
                "class_type": "BasicScheduler"
            },
            "14": {
                "inputs": {
                    "sampler_name": "euler_ancestral"
                },
                "class_type": "KSamplerSelect"
            },
            "37": {
                "inputs": {
                    "noise_seed": seed
                },
                "class_type": "RandomNoise"
            },
            "130": {
                "inputs": {
                    "model": ["103", 0],
                    "positive": ["87", 0],
                    "negative": ["88", 0],
                    "cfg": 3,
                    "stg": 1,
                    "rescale": 0.75
                },
                "class_type": "STGGuider"
            },
            "104": {
                "inputs": {
                    "sigmas": ["81", 0],
                    "sigma_shift": ["103", 2],
                    "stretch": True,
                    "terminal": 0.1
                },
                "class_type": "LTXVShiftSigmas"
            },
            "36": {
                "inputs": {
                    "noise": ["37", 0],
                    "guider": ["130", 0],
                    "sampler": ["14", 0],
                    "sigmas": ["104", 0],
                    "latent_image": ["103", 1]
                },
                "class_type": "SamplerCustomAdvanced"
            },
            "8": {
                "inputs": {
                    "samples": ["36", 1],
                    "vae": ["102", 1]
                },
                "class_type": "VAEDecode"
            },
            "60": {
                "inputs": {
                    "frame_rate": fps,
                    "loop_count": 0,
                    "filename_prefix": f"{folder_name}/{base_filename}",
                    "format": "video/h264-mp4",
                    "pix_fmt": "yuv420p",
                    "crf": 19,
                    "save_metadata": False,
                    "trim_to_audio": False,
                    "pingpong": False,
                    "save_output": True,
                    "images": ["8", 0]
                },
                "class_type": "VHS_VideoCombine"
            }
        }
        return workflow
    async def generate_video(self, image_path: str, prompt: str, 
                           folder_name: str = DEFAULT_FOLDER_NAME,
                           base_filename: str = DEFAULT_BASE_FILENAME, 
                           seed: Optional[int] = None,
                           frame_length: int = DEFAULT_FRAME_LENGTH, 
                           width: int = DEFAULT_WIDTH, 
                           height: int = DEFAULT_HEIGHT,
                           fps: int = DEFAULT_FPS) -> str:
        folder_path = os.path.join(self.base_output_dir, folder_name)
        os.makedirs(folder_path, exist_ok=True)
        existing_files = self._get_existing_files(folder_name)
        uploaded_image = await self._upload_image(image_path)

        workflow = self._create_workflow(
            prompt=prompt,
            uploaded_image=uploaded_image,
            folder_name=folder_name,
            base_filename=base_filename,
            seed=seed,
            frame_length=frame_length,
            width=width,
            height=height,
            fps=fps
        )

        prompt_url = f"{self.server_url}/prompt"
        response = requests.post(prompt_url, json={
            "prompt": workflow,
            "client_id": self.client_id
        })
        
        if response.status_code != 200:
            raise Exception(f"Failed to send prompt: {response.text}")

        self._connect_websocket()
        
        try:
            while True:
                msg = json.loads(self.ws.recv())
                if msg["type"] == "executed":
                    try:
                        video_path = self._wait_for_new_file(folder_name, existing_files)
                        print(f"Generated video path: {video_path}")
                        return video_path
                    except TimeoutError as e:
                        raise Exception("Failed to detect new video file") from e
        finally:
            self.ws.close()

    async def generate_video_with_ratio(self, image_path: str, prompt: str, 
                                      folder_name: str = DEFAULT_FOLDER_NAME,
                                      base_filename: str = DEFAULT_BASE_FILENAME, 
                                      seed: Optional[int] = None,
                                      frame_length: int = DEFAULT_FRAME_LENGTH, 
                                      max_size: int = DEFAULT_MAX_SIZE, 
                                      fps: int = DEFAULT_FPS) -> str:
        width, height = self._calculate_dimensions(image_path, max_size)
        print(f"Resizing image to {width}x{height} to maintain aspect ratio")
        
        return await self.generate_video(
            image_path=image_path,
            prompt=prompt,
            folder_name=folder_name,
            base_filename=base_filename,
            seed=seed,
            frame_length=frame_length,
            width=width,
            height=height,
            fps=fps
        )

async def main():
    client = LTXVClient()
    image_path = "Cammy_00013_.png"
    prompt = "A soldier girl walking down the street. A soldier girl looking around and exploring."
    
    try:
        video_path = await client.generate_video_with_ratio(
            image_path=image_path,
            prompt=prompt,
            folder_name="LTXVideo",
            base_filename="test_video"
        )
        print(f"Video generation completed. File saved at: {video_path}")
    except Exception as e:
        print(f"Error generating video: {str(e)}")

if __name__ == "__main__":
    import asyncio
    asyncio.run(main())