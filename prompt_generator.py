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
        
        # 수정된 시스템 프롬프트
        self.system_prompt = """You are an expert marketing strategist specializing in creating video advertisement prompts that resonate with specific target audiences. Your task is to generate prompts that will create videos that effectively engage and appeal to the defined demographic, considering their preferences, behaviors, and cultural context. Focus on:

1. Target Audience Psychology:
   - Understanding their interests, preferences, and consumption patterns
   - Identifying engaging visual elements and storytelling approaches
   - Considering cultural references and trends that resonate with them

2. Marketing Strategy:
   - Creating scenes that appeal to the target demographic without necessarily featuring them
   - Incorporating relevant lifestyle elements and aspirational content
   - Using appropriate pacing and energy levels for the target age group

3. Visual Style Guide:
   - Color schemes that appeal to the target demographic
   - Music and sound design preferences of the audience
   - Visual effects and transitions that capture their attention

4. Brand Integration:
   - Seamlessly incorporating products in contexts relevant to the audience
   - Using language and tone that resonates with the target group
   - Creating scenarios that demonstrate value proposition for the specific demographic

Remember: The goal is not to show the target demographic in the video, but to create content that naturally attracts and engages them based on their preferences and interests."""

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
        """개선된 프롬프트 준비 로직"""
        try:
            prompt_data = json.loads(input_data)
            
            structured_prompt = "Design a video advertisement concept with the following strategic focus:\n\n"
            
            # 타겟 분석 및 전략 수립
            if prompt_data.get('gender') or prompt_data.get('ageGroup'):
                structured_prompt += "Target Audience Analysis:\n"
                
                # 연령대별 특성 고려
                age_group = prompt_data.get('ageGroup', '')
                gender = prompt_data.get('gender', '')
                
                if age_group and gender:
                    structured_prompt += f"Create content that appeals to {age_group} {gender} audience by considering:\n"
                    structured_prompt += "- Their typical media consumption habits\n"
                    structured_prompt += "- Current trends and interests in this demographic\n"
                    structured_prompt += "- Preferred visual and audio styles\n"
                    structured_prompt += "- Popular cultural references and themes\n\n"
            
            # 제품 카테고리 컨텍스트
            if prompt_data.get('productCategory'):
                structured_prompt += f"Product Category Context:\n"
                structured_prompt += f"Product: {prompt_data['productCategory']}\n"
                structured_prompt += "Consider:\n"
                structured_prompt += "- How this demographic typically interacts with this product category\n"
                structured_prompt += "- Key purchase motivators for this audience\n"
                structured_prompt += "- Preferred product presentation style\n\n"
            
            # 시즌/이벤트 마케팅 전략
            if prompt_data.get('seasonEvent'):
                structured_prompt += f"Seasonal/Event Strategy:\n"
                structured_prompt += f"Context: {prompt_data['seasonEvent']}\n"
                structured_prompt += "Incorporate:\n"
                structured_prompt += "- Seasonal elements that resonate with the target audience\n"
                structured_prompt += "- Event-specific motivations and emotions\n\n"
            
            # 광고 톤 및 스타일
            if prompt_data.get('adTone'):
                structured_prompt += f"Creative Direction:\n"
                structured_prompt += f"Tone: {prompt_data['adTone']}\n"
                structured_prompt += "Ensure the tone aligns with:\n"
                structured_prompt += "- Target audience's communication preferences\n"
                structured_prompt += "- Brand personality and message\n\n"
            
            # 추가 요구사항
            if prompt_data.get('additionalRequests'):
                structured_prompt += f"Additional Strategic Considerations:\n{prompt_data['additionalRequests']}\n\n"
            
            structured_prompt += "\nCreate a video concept that naturally attracts this audience through their preferred visual styles, storytelling approaches, and cultural references, without explicitly featuring them in the advertisement."
            
            return structured_prompt
        
        except json.JSONDecodeError:
            return input_data

    def generate(self, prompt_data: str) -> str:
        """GPT를 사용하여 프롬프트 생성"""
        try:
            processed_prompt = self._prepare_prompt(prompt_data)
            
            # OpenAI API 호출
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