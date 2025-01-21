import json
import random
import requests
import websocket
import uuid
import os
import glob
import time
from typing import Dict, Any, Optional, Set
from config import load_config

class HunyuanVideoClient:
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

    def _get_existing_files(self, folder_name: str) -> Set[str]:
        folder_path = os.path.join(self.base_output_dir, folder_name)
        if not os.path.exists(folder_path):
            os.makedirs(folder_path, exist_ok=True)
        pattern = os.path.join(folder_path, "*.mp4")
        return set(os.path.basename(f) for f in glob.glob(pattern))

    def _wait_for_new_file(self, folder_name: str, existing_files: Set[str], timeout: int = 180) -> str:
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

    def _get_upscale_resolution(self, width: int, height: int) -> tuple[int, int]:
        aspect_ratio = width / height

        if abs(aspect_ratio - 16/9) < 0.01:
            return (1920, 1080) if width > height else (1080, 1920)
        
        elif abs(aspect_ratio - 4/3) < 0.01:
            return (1600, 1200) if width > height else (1200, 1600)
        
        elif abs(aspect_ratio - 1) < 0.01:
            return (1440, 1440)
        
        return (1920, 1080) if width > height else (1080, 1920)

    def _create_workflow(self, prompt: str, folder_name: str, base_filename: str = "video", 
                        seed: int = None, frame_length: int = 73, width: int = 848, height: int = 480,
                        enable_upscale: bool = False) -> Dict[str, Any]:
        if seed is None:
            seed = random.randint(1, 999999999999999)
            
        print(f"Using seed: {seed}")

        workflow = {
            "10": {
                "inputs": {
                    "vae_name": "hunyuan_video_vae_bf16.safetensors"
                },
                "class_type": "VAELoader"
            },
            "11": {
                "inputs": {
                    "clip_name1": "clip_l.safetensors",
                    "clip_name2": "llava_llama3_fp8_scaled.safetensors",
                    "type": "hunyuan_video"
                },
                "class_type": "DualCLIPLoader"
            },
            "12": {
                "inputs": {
                    "unet_name": "hunyuan_video_t2v_720p_bf16.safetensors",
                    "weight_dtype": "default"
                },
                "class_type": "UNETLoader"
            },
            "13": {
                "inputs": {
                    "noise": ["25", 0],
                    "guider": ["22", 0],
                    "sampler": ["16", 0],
                    "sigmas": ["17", 0],
                    "latent_image": ["45", 0]
                },
                "class_type": "SamplerCustomAdvanced"
            },
            "16": {
                "inputs": {
                    "sampler_name": "euler"
                },
                "class_type": "KSamplerSelect"
            },
            "17": {
                "inputs": {
                    "scheduler": "simple",
                    "steps": 8,
                    "denoise": 1,
                    "model": ["12", 0]
                },
                "class_type": "BasicScheduler"
            },
            "22": {
                "inputs": {
                    "model": ["79", 0],
                    "conditioning": ["26", 0]
                },
                "class_type": "BasicGuider"
            },
            "25": {
                "inputs": {
                    "noise_seed": seed
                },
                "class_type": "RandomNoise"
            },
            "26": {
                "inputs": {
                    "guidance": 6,
                    "conditioning": ["44", 0]
                },
                "class_type": "FluxGuidance"
            },
            "44": {
                "inputs": {
                    "text": prompt,
                    "clip": ["11", 0]
                },
                "class_type": "CLIPTextEncode"
            },
            "45": {
                "inputs": {
                    "width": width,
                    "height": height,
                    "length": frame_length,
                    "batch_size": 1
                },
                "class_type": "EmptyHunyuanLatentVideo"
            },
            "67": {
                "inputs": {
                    "shift": 7,
                    "model": ["12", 0]
                },
                "class_type": "ModelSamplingSD3"
            },
            "73": {
                "inputs": {
                    "tile_size": 256,
                    "overlap": 64,
                    "temporal_size": 64,
                    "temporal_overlap": 8,
                    "samples": ["13", 0],
                    "vae": ["10", 0]
                },
                "class_type": "VAEDecodeTiled"
            },
            "79": {
                "inputs": {
                    "lora_name": "hyvideo_FastVideo_LoRA-fp8.safetensors",
                    "strength_model": 0.8,
                    "model": ["67", 0]
                },
                "class_type": "LoraLoaderModelOnly"
            }
        }

        if enable_upscale:
            workflow["87"] = {
                "inputs": {
                    "model_name": "4x_foolhardy_Remacri.pth"
                },
                "class_type": "UpscaleModelLoader"
            }
            
            workflow["88"] = {
                "inputs": {
                    "upscale_model": ["87", 0],
                    "image": ["73", 0]
                },
                "class_type": "ImageUpscaleWithModel"
            }

            target_width, target_height = self._get_upscale_resolution(width, height)
            workflow["89"] = {
                "inputs": {
                    "upscale_method": "lanczos",
                    "width": target_width,
                    "height": target_height,
                    "crop": "center",
                    "image": ["88", 0]
                },
                "class_type": "ImageScale"
            }

            video_input = ["89", 0]
        else:
            video_input = ["73", 0]

        workflow["75"] = {
            "inputs": {
                "frame_rate": 24,
                "loop_count": 0,
                "filename_prefix": f"{folder_name}/{base_filename}",
                "format": "video/nvenc_h264-mp4",
                "pix_fmt": "yuv420p",
                "bitrate": 10,
                "megabit": True,
                "save_metadata": False,
                "pingpong": False,
                "save_output": True,
                "images": video_input
            },
            "class_type": "VHS_VideoCombine"
        }

        return workflow

    def generate_video(self, prompt: str, folder_name: str = "KTaivle", base_filename: str = "video",
                      seed: Optional[int] = None, frame_length: int = 73, 
                      width: int = 848, height: int = 480, enable_upscale: bool = False) -> str:
        folder_path = os.path.join(self.base_output_dir, folder_name)
        os.makedirs(folder_path, exist_ok=True)
        
        existing_files = self._get_existing_files(folder_name)

        workflow = self._create_workflow(prompt, folder_name, base_filename, seed, frame_length, width, height, enable_upscale)
        
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

def main():
    client = HunyuanVideoClient()
    prompt = "man drinking coffee in cafe bright morning"
    
    try:
        video_path = client.generate_video(
            prompt=prompt,
            folder_name="KTaivle",
            base_filename="video"
        )
        print(f"Video generation completed. File saved at: {video_path}")
    except Exception as e:
        print(f"Error generating video: {str(e)}")

if __name__ == "__main__":
    main()