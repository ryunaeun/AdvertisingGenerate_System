import openai
import os
from typing import Dict, Optional
import json
from datetime import datetime

class PromptGenerator:
    def __init__(self):
        # API 키 파일에서 읽기
        self.api_key = self._load_api_key()
        if not self.api_key:
            raise ValueError("Failed to load API key from API_KEY.txt")
        
        # OpenAI 클라이언트 초기화
        self.client = openai.OpenAI(api_key=self.api_key)
        
        # 시스템 프롬프트 설정
        self.system_prompt = """You are an expert at creating detailed prompts for video advertisements. Your task is to generate clear, specific, and creative prompts that will be used to generate video content. Focus on these aspects:
1. Visual elements and scenes
2. Style and atmosphere
3. Color schemes and lighting
4. Camera movements and transitions
5. Target audience considerations
6. Brand tone and message

The prompt should be optimized for AI video generation and maintain consistency with the target audience and marketing goals."""

    def _load_api_key(self) -> str:
        """API_KEY.txt 파일에서 API 키를 읽어옴"""
        try:
            script_dir = os.path.dirname(os.path.abspath(__file__))
            api_key_path = os.path.join(script_dir, 'API_KEY.txt')
            
            if not os.path.exists(api_key_path):
                raise FileNotFoundError(f"API_KEY.txt not found at {api_key_path}")
            
            with open(api_key_path, 'r', encoding='utf-8') as f:
                content = f.read().strip()
                
            for line in content.split('\n'):
                if line.startswith('CHAT_GPT'):
                    api_key = line.split('=')[1].strip().strip("'").strip('"')
                    return api_key
                    
            raise ValueError("CHAT_GPT key not found in API_KEY.txt")
            
        except Exception as e:
            print(f"Error loading API key: {str(e)}")
            raise

    def _prepare_prompt(self, input_data: str) -> Dict:
        """입력된 JSON 문자열을 파싱하고 구조화된 프롬프트를 준비"""
        try:
            prompt_data = json.loads(input_data)
            
            structured_prompt = "Create a video advertisement with the following specifications:\n\n"
            
            # 타겟 정보 추가
            target_info = []
            if prompt_data.get('gender'):
                target_info.append(f"Gender: {prompt_data['gender']}")
            if prompt_data.get('ageGroup'):
                target_info.append(f"Age Group: {prompt_data['ageGroup']}")
            if target_info:
                structured_prompt += "Target Audience:\n" + "\n".join(target_info) + "\n\n"
            
            # 제품 카테고리 추가
            if prompt_data.get('productCategory'):
                structured_prompt += f"Product Category: {prompt_data['productCategory']}\n\n"
            
            # 시즌/이벤트 정보 추가
            if prompt_data.get('seasonEvent'):
                structured_prompt += f"Seasonal Context: {prompt_data['seasonEvent']}\n\n"
            
            # 광고 톤 추가
            if prompt_data.get('adTone'):
                structured_prompt += f"Advertisement Tone: {prompt_data['adTone']}\n\n"
            
            # 추가 요구사항 추가
            if prompt_data.get('additionalRequests'):
                structured_prompt += f"Additional Requirements:\n{prompt_data['additionalRequests']}\n"
            
            return structured_prompt
        except json.JSONDecodeError:
            return input_data

    def generate(self, prompt_data: str) -> str:
        """GPT를 사용하여 프롬프트 생성"""
        try:
            processed_prompt = self._prepare_prompt(prompt_data)
            
            # 새로운 OpenAI API 버전으로 호출
            response = self.client.chat.completions.create(
                model="gpt-4",
                messages=[
                    {"role": "system", "content": self.system_prompt},
                    {"role": "user", "content": processed_prompt}
                ],
                temperature=0.7,
                max_tokens=1000,
                top_p=0.9,
                frequency_penalty=0.3,
                presence_penalty=0.3
            )
            
            # 새로운 응답 구조에 맞게 수정
            generated_prompt = response.choices[0].message.content.strip()
            
            # 로그 기록
            self._log_generation(prompt_data, generated_prompt)
            
            return generated_prompt
            
        except Exception as e:
            print(f"Error generating prompt: {str(e)}")
            raise

    def _log_generation(self, input_prompt: str, generated_prompt: str):
        """프롬프트 생성 로그를 기록"""
        try:
            log_dir = "prompt_logs"
            os.makedirs(log_dir, exist_ok=True)
            
            timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
            log_file = os.path.join(log_dir, f"prompt_generation_{timestamp}.log")
            
            with open(log_file, 'w', encoding='utf-8') as f:
                f.write("=== Input Prompt ===\n")
                f.write(input_prompt)
                f.write("\n\n=== Generated Prompt ===\n")
                f.write(generated_prompt)
                
        except Exception as e:
            print(f"Error writing log: {str(e)}")