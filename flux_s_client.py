import json
import websocket
import requests
import uuid
import os
import glob
import time
from config import load_config
from typing import Dict, Any, Optional, Set, List

class FluxImageClient:
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
        pattern = os.path.join(folder_path, "*.png")
        return set(os.path.basename(f) for f in glob.glob(pattern))

    def _wait_for_new_files(self, folder_name: str, existing_files: Set[str], count: int = 4, timeout: int = 60) -> List[str]:
        """Wait for multiple new files to be generated"""
        start_time = time.time()
        folder_path = os.path.join(self.base_output_dir, folder_name)
        new_files = []
        
        while time.time() - start_time < timeout and len(new_files) < count:
            current_files = self._get_existing_files(folder_name)
            new_files = sorted([
                os.path.join(folder_path, f) 
                for f in current_files - existing_files
            ], key=lambda x: os.path.getctime(x))
            if len(new_files) < count:
                time.sleep(0.5)
                
        if len(new_files) < count:
            raise TimeoutError(f"Only {len(new_files)} of {count} files were detected within timeout")
            
        return new_files

    def _create_workflow(self, prompt: str, folder_name: str, base_filename: str = "example",
                        seed: Optional[int] = None, batch_size: int = 4) -> Dict[str, Any]:
        workflow = {
            "8": {
                "inputs": {
                    "samples": ["31", 0],
                    "vae": ["30", 2]
                },
                "class_type": "VAEDecode"
            },
            "9": {
                "inputs": {
                    "filename_prefix": f"{folder_name}/{base_filename}",
                    "images": ["8", 0]
                },
                "class_type": "SaveImage"
            },
            "27": {
                "inputs": {
                    "width": 1024,
                    "height": 1024,
                    "batch_size": batch_size
                },
                "class_type": "EmptySD3LatentImage"
            },
            "30": {
                "inputs": {
                    "ckpt_name": "flux_schnell.safetensors"
                },
                "class_type": "CheckpointLoaderSimple"
            },
            "31": {
                "inputs": {
                    "seed": seed if seed is not None else int(time.time() * 1000) % (2**32),
                    "steps": 6,
                    "cfg": 1,
                    "sampler_name": "euler",
                    "scheduler": "simple",
                    "denoise": 1,
                    "model": ["30", 0],
                    "positive": ["43", 0],
                    "negative": ["33", 0],
                    "latent_image": ["27", 0]
                },
                "class_type": "KSampler"
            },
            "33": {
                "inputs": {
                    "text": "",
                    "clip": ["30", 1]
                },
                "class_type": "CLIPTextEncode"
            },
            "37": {
                "inputs": {
                    "string": ["42", 0],
                    "old": "video",
                    "new": "image"
                },
                "class_type": "String Replace (mtb)"
            },
            "38": {
                "inputs": {
                    "string": ["37", 0],
                    "old": "film",
                    "new": "image"
                },
                "class_type": "String Replace (mtb)"
            },
            "39": {
                "inputs": {
                    "string": ["38", 0],
                    "old": "footage",
                    "new": "image"
                },
                "class_type": "String Replace (mtb)"
            },
            "42": {
                "inputs": {
                    "action": "append",
                    "tidy_tags": "no",
                    "text_a": prompt,
                    "result": prompt
                },
                "class_type": "StringFunction|pysssss"
            },
            "43": {
                "inputs": {
                    "text": ["39", 0],
                    "clip": ["30", 1]
                },
                "class_type": "CLIPTextEncode"
            }
        }
        return workflow

    def generate_images(self, prompt: str, folder_name: str = "flux_examples", 
                       base_filename: str = "example", seed: Optional[int] = None, 
                       batch_size: int = 4) -> List[str]:
        """
        Generate multiple images from a prompt
        Returns a list of file paths to the generated images
        """
        folder_path = os.path.join(self.base_output_dir, folder_name)
        os.makedirs(folder_path, exist_ok=True)
        
        existing_files = self._get_existing_files(folder_name)
        workflow = self._create_workflow(prompt, folder_name, base_filename, seed, batch_size)
        
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
                        image_paths = self._wait_for_new_files(folder_name, existing_files, batch_size)
                        print(f"Generated image paths: {image_paths}")
                        return image_paths
                    except TimeoutError as e:
                        raise Exception("Failed to detect new image files") from e
        finally:
            self.ws.close()

def main():
    client = FluxImageClient()
    prompt = "Create a dynamic and engaging commercial image for Sony headphones..."
    
    try:
        image_paths = client.generate_images(
            prompt=prompt,
            folder_name="flux_examples",
            base_filename="example"
        )
        print(f"Image generation completed. Files saved at: {image_paths}")
    except Exception as e:
        print(f"Error generating images: {str(e)}")

if __name__ == "__main__":
    main()