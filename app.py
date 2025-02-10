from flask import Flask, request, jsonify, send_file, render_template
from flask_cors import CORS  # 추가
from hunyuan_client import HunyuanVideoClient
from flux_s_client import FluxImageClient
from config import load_config
from prompt_generator import PromptGenerator
from datetime import datetime
from ad_recommender import AdRecommender
from ltxv_client import LTXVClient
from werkzeug.utils import secure_filename
from PIL import Image 
import asyncio
import random
import os
import time
import json
import openpyxl
import csv
import pandas as pd

app = Flask(__name__)
CORS(app)  # CORS 허용 설정 추가
video_client = HunyuanVideoClient()
image_client = FluxImageClient()
prompt_generator = PromptGenerator()
ad_recommender = AdRecommender()
ltxv_client = LTXVClient()
BASE_UPLOAD_FOLDER = r"D:\ComfyUI_windows_portable\ComfyUI\input"
ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg'}
OUTPUT_DIR = r"D:\ComfyUI_windows_portable\ComfyUI\output"

@app.route('/')
def index():
    return render_template('prompt_gen.html')

@app.route('/video_gen')
def video_gen():
    return render_template('video_gen.html')

@app.route('/txt2img_gen')
def txt2img_gen():
    return render_template('txt2img_gen.html')

@app.route('/prompt_gen')
def prompt_gen():
    return render_template('prompt_gen.html')

@app.route('/generate_prompt', methods=['POST'])
def generate_prompt():
    """프롬프트 생성 및 광고 추천"""
    try:
        # 입력 데이터 검증
        data = request.json
        if not data:
            return jsonify({'error': 'No data provided'}), 400

        if not isinstance(data, dict):
            return jsonify({'error': 'Invalid data format'}), 400

        # 필수 필드 확인
        required_fields = ['gender', 'ageGroup', 'productCategory', 'userId']  # userId 추가
        missing_fields = [field for field in required_fields if field not in data]
        if missing_fields:
            return jsonify({'error': f'Missing required fields: {", ".join(missing_fields)}'}), 400

        try:
            # 1. 광고 추천 모델 실행
            recommendations = ad_recommender.predict(data)
            print(f"Generated recommendations: {recommendations}")  # 디버깅용

            # 2. 추천 정보를 JSON 파일로 저장
            if recommendations:
                # 사용자별 디렉토리 생성
                user_dir = os.path.join(OUTPUT_DIR, data['userId'], 'recommendations')
                os.makedirs(user_dir, exist_ok=True)

                # 현재 시간을 파일명에 포함
                timestamp = datetime.now().strftime("%Y%m%d-%H%M%S")
                filename = f"recommendation_{timestamp}.json"
                file_path = os.path.join(user_dir, filename)

                # 저장할 추천 데이터 구성
                recommendation_data = {
                    'timestamp': timestamp,
                    'user_input': {
                        'gender': data['gender'],
                        'ageGroup': data['ageGroup'],
                        'productCategory': data['productCategory']
                    },
                    'recommendations': {
                        'time': recommendations['recommended_time'],
                        'adtype': recommendations['recommended_adtype']
                    }
                }

                # JSON 파일 저장
                with open(file_path, 'w', encoding='utf-8') as f:
                    json.dump(recommendation_data, f, ensure_ascii=False, indent=2)

        except Exception as e:
            print(f"Error in ad recommendation: {str(e)}")
            recommendations = None

        # 3. GPT 프롬프트 생성
        try:
            generated_prompt = prompt_generator.generate(json.dumps(data))
            print("Generated GPT prompt successfully")  # 디버깅용
        except Exception as e:
            print(f"Error in prompt generation: {str(e)}")
            return jsonify({'error': 'Failed to generate prompt'}), 500

        # 4. 응답 반환
        response = {
            'success': True,
            'generated_prompt': generated_prompt,
            'recommendations': {
                'time': recommendations['recommended_time'] if recommendations else None,
                'adtype': recommendations['recommended_adtype'] if recommendations else None,
                'formatted_text': ad_recommender.format_recommendation(recommendations) if recommendations else ""
            }
        }

        return jsonify(response)

    except Exception as e:
        print(f"Unexpected error in generate_prompt: {str(e)}")
        return jsonify({'error': str(e)}), 500

# 광고 추천 결과만 별도로 받을 수 있는 엔드포인트
@app.route('/get_recommendations', methods=['POST'])
def get_recommendations():
    """광고 추천 정보만 반환"""
    try:
        data = request.json
        if not data:
            return jsonify({'error': 'No data provided'}), 400

        # 광고 추천 모델 실행
        recommendations = ad_recommender.predict(data)
        
        return jsonify({
            'success': True,
            'recommendations': recommendations
        })

    except Exception as e:
        print(f"Error in get_recommendations: {str(e)}")
        return jsonify({'error': str(e)}), 500

@app.route('/generate_examples', methods=['POST'])
def generate_examples():
    data = request.json
    prompt = data.get('prompt')
    folder_name = data.get('savePath', 'flux_examples')
    
    if not prompt:
        return jsonify({'error': 'Prompt is required'}), 400
        
    try:
        # ComfyUI 큐가 처리될 시간을 주기 위해 잠시 대기
        time.sleep(1)
        
        # Generate 4 example images
        image_paths = image_client.generate_images(
            prompt=prompt,
            folder_name=folder_name,
            base_filename="example",
            batch_size=4
        )
        
        # 모든 이미지 파일이 완전히 생성될 때까지 대기
        for path in image_paths:
            while not os.path.exists(path):
                time.sleep(0.5)
            
            # 파일이 완전히 쓰여질 때까지 추가 대기
            time.sleep(1)
        
        # Convert full paths to relative paths for frontend
        relative_paths = [
            os.path.join(folder_name, os.path.basename(path))
            for path in image_paths
        ]
        
        print(f"Generated image paths: {relative_paths}")
        
        return jsonify({
            'success': True,
            'image_paths': relative_paths
        })
    except Exception as e:
        print(f"Error generating images: {str(e)}")
        return jsonify({'error': str(e)}), 500

@app.route('/generate', methods=['POST'])
def generate_video():
    data = request.json
    prompt = data.get('prompt')
    use_random_seed = data.get('useRandomSeed')
    frame_length = data.get('frameLength', 73)
    width = data.get('width', 848)
    height = data.get('height', 480)
    folder_name = data.get('savePath', 'KTaivle')
    enable_upscale = data.get('enableUpscale', False)
    seed = None
    
    if not prompt:
        return jsonify({'error': 'Prompt is required'}), 400
        
    if use_random_seed:
        seed = random.randint(1, 999999999999999)
    else:
        try:
            seed = int(data.get('seed'))
            if not (1 <= seed <= 999999999999999):
                return jsonify({'error': 'Seed must be between 1 and 999999999999999'}), 400
        except (TypeError, ValueError):
            return jsonify({'error': 'Invalid seed value'}), 400
    
    try:
        # ComfyUI 큐가 처리될 시간을 주기 위해 잠시 대기
        time.sleep(1)
        
        video_path = video_client.generate_video(
            prompt=prompt,
            folder_name=folder_name,
            base_filename="video",
            seed=seed,
            frame_length=frame_length,
            width=width,
            height=height,
            enable_upscale=enable_upscale
        )
        
        # 비디오 생성이 완료될 때까지 대기
        while not os.path.exists(video_path):
            time.sleep(0.5)
            
        # 파일이 완전히 쓰여질 때까지 추가 대기
        time.sleep(2)
        
        filename = os.path.basename(video_path)
        folder = os.path.basename(os.path.dirname(video_path))
        return jsonify({
            'success': True,
            'seed': seed,
            'filename': filename,
            'folder': folder
        })
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/output/<path:filepath>')
def serve_file(filepath):
    base_output_dir = r"D:\ComfyUI_windows_portable\ComfyUI\output"
    
    full_path = os.path.join(base_output_dir, filepath)
    print(f"Serving file from: {full_path}")
    
    if not os.path.exists(full_path):
        print(f"File not found: {full_path}")
        return jsonify({'error': 'File not found'}), 404
    
    try:
        # Determine the mimetype based on file extension
        mimetype = 'video/mp4' if filepath.endswith('.mp4') else 'image/png'
        
        return send_file(
            full_path,
            mimetype=mimetype,
            as_attachment=False,
            download_name=os.path.basename(filepath)
        )
    except Exception as e:
        print(f"Error serving file: {str(e)}")
        return jsonify({'error': str(e)}), 500

@app.route('/save_prompt', methods=['POST'])
def save_prompt():
    try:
        data = request.json
        user_id = data.get('userId')
        file_name = data.get('fileName')
        content = data.get('content')

        # 필수 데이터 검증
        if not all([user_id, file_name, content]):
            return jsonify({'error': 'Missing required data'}), 400

        # Prompt 디렉토리 경로 생성
        prompt_dir = os.path.join(OUTPUT_DIR, user_id, 'Prompt')
        os.makedirs(prompt_dir, exist_ok=True)

        # 파일명에 타임스탬프 추가
        timestamp = datetime.now().strftime("%Y%m%d-%H%M%S")
        full_file_name = f"{file_name}-{timestamp}.txt"
        file_path = os.path.join(prompt_dir, full_file_name)

        # 파일 저장
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)

        return jsonify({
            'success': True,
            'message': 'Prompt saved successfully',
            'file_name': full_file_name
        }), 200

    except Exception as e:
        print(f"Error saving prompt: {str(e)}")
        return jsonify({'error': str(e)}), 500

@app.route('/load_prompts', methods=['GET'])
def load_prompts():
    try:
        user_id = request.args.get('userId')

        if not user_id:
            return jsonify({'error': 'Missing user ID'}), 400

        prompt_dir = os.path.join(OUTPUT_DIR, user_id, 'Prompt')

        # 디렉토리가 없으면 빈 리스트 반환
        if not os.path.exists(prompt_dir):
            return jsonify({'files': []}), 200

        # .txt 파일만 필터링하여 목록 반환
        files = [f for f in os.listdir(prompt_dir) if f.endswith('.txt')]
        # 파일 목록을 날짜순으로 정렬 (최신 순)
        files.sort(reverse=True)

        return jsonify({'files': files}), 200

    except Exception as e:
        print(f"Error loading prompts: {str(e)}")
        return jsonify({'error': str(e)}), 500

@app.route('/load_prompt', methods=['GET'])
def load_prompt():
    try:
        user_id = request.args.get('userId')
        file_name = request.args.get('fileName')

        if not all([user_id, file_name]):
            return jsonify({'error': 'Missing required parameters'}), 400

        file_path = os.path.join(OUTPUT_DIR, user_id, 'Prompt', file_name)
        
        if not os.path.exists(file_path):
            return jsonify({'error': 'File not found'}), 404

        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()

        return jsonify({
            'success': True,
            'content': content
        })

    except Exception as e:
        print(f"Error loading prompt: {str(e)}")
        return jsonify({'error': str(e)}), 500

@app.route('/update_target_settings', methods=['POST'])
def update_target_settings():
    try:
        if 'file' not in request.files:
            return jsonify({'error': 'No file uploaded'}), 400
            
        file = request.files['file']
        if file.filename == '':
            return jsonify({'error': '선택된 파일이 없습니다'}), 400
            
        file_ext = os.path.splitext(file.filename)[1].lower()
        
        if file_ext not in ['.xlsx', '.xls', '.csv']:
            return jsonify({'error': '잘못된 파일 형식입니다. Excel 또는 CSV 파일을 업로드해주세요'}), 400

        settings = {"targetSettings": {}}
        
        # 카테고리별 ID 매핑 정의
        category_mapping = {
            'gender': 'targetGender',
            'ageGroup': 'targetAge',
            'productCategory': 'productCategory',
            'seasonEvent': 'seasonEvent',
            'adTone': 'adTone'
        }
        
        # 카테고리별 레이블 매핑 정의
        label_mapping = {
            'gender': '성별',
            'ageGroup': '나이',
            'productCategory': '상품 카테고리',
            'seasonEvent': '행사 시즌/이벤트',
            'adTone': '광고 분위기'
        }

        if file_ext == '.csv':
            # CSV 파일 처리
            import pandas as pd
            import io
            
            # CSV 파일을 DataFrame으로 읽기
            df = pd.read_csv(io.StringIO(file.stream.read().decode("UTF8")), dtype=str)
            
            # 필수 컬럼 확인
            required_columns = ['category', 'value', 'label']
            if not all(col in df.columns for col in required_columns):
                return jsonify({'error': 'CSV 파일은 반드시 category, value, label 컬럼을 포함해야 합니다'}), 400
            
            # DataFrame을 settings 구조로 변환
            for category in df['category'].unique():
                if category not in category_mapping:
                    continue
                    
                category_df = df[df['category'] == category]
                options = []
                
                # 각 카테고리의 옵션 추가
                for _, row in category_df.iterrows():
                    if pd.notna(row['value']) or row['value'] == '':  # 빈 문자열도 허용
                        options.append({
                            "value": str(row['value']),
                            "label": str(row['label']).strip() if pd.notna(row['label']) else str(row['value']).strip()
                        })
                
                # settings 딕셔너리에 카테고리 추가
                settings["targetSettings"][category] = {
                    "id": category_mapping[category],
                    "label": label_mapping[category],
                    "options": options
                }
                
        else:
            # Excel 파일 처리
            wb = openpyxl.load_workbook(file)
            
            # Process each sheet (assuming sheet names match setting categories)
            for sheet_name in wb.sheetnames:
                if sheet_name not in category_mapping:
                    continue
                    
                sheet = wb[sheet_name]
                options = []
                
                # Skip header row and process data rows
                for row in list(sheet.rows)[1:]:
                    if row[0].value is not None or row[0].value == '':  # 빈 문자열도 허용
                        options.append({
                            "value": str(row[0].value if row[0].value is not None else ''),
                            "label": str(row[1].value).strip() if row[1].value else str(row[0].value).strip()
                        })
                
                # Add to settings dictionary with correct mappings
                settings["targetSettings"][sheet_name] = {
                    "id": category_mapping[sheet_name],
                    "label": label_mapping[sheet_name],
                    "options": options
                }
        
        # Validate and add missing categories
        for category, id_value in category_mapping.items():
            if category not in settings["targetSettings"]:
                settings["targetSettings"][category] = {
                    "id": id_value,
                    "label": label_mapping[category],
                    "options": []
                }
        
        # Save to JSON file
        json_path = os.path.join(app.static_folder, 'config', 'target_settings.json')
        os.makedirs(os.path.dirname(json_path), exist_ok=True)
        
        with open(json_path, 'w', encoding='utf-8') as f:
            json.dump(settings, f, indent=2, ensure_ascii=False)
        
        return jsonify({'success': True, 'message': 'Settings updated successfully'})
        
    except Exception as e:
        print(f"Error updating settings: {str(e)}")
        return jsonify({'error': str(e)}), 500

@app.route('/gallery_check', methods=['GET'])
def gallery_check():
    try:
        user_id = request.args.get('userId', 'KTaivle')
        base_path = os.path.join(OUTPUT_DIR, user_id, 'videos')
        
        if not os.path.exists(base_path):
            return jsonify({'error': 'Directory not found'}), 404
            
        files = []
        video_files = [f for f in os.listdir(base_path) if f.endswith(('.mp4', '.MP4'))]
        
        for video_filename in video_files:
            file_path = os.path.join(base_path, video_filename)
            base_name = os.path.splitext(video_filename)[0]
            
            # Check for thumbnail with various image extensions
            thumbnail_path = None
            for ext in ['.jpg', '.jpeg', '.png', '.JPG', '.JPEG', '.PNG']:
                potential_thumbnail = os.path.join(base_path, base_name + ext)
                if os.path.exists(potential_thumbnail):
                    thumbnail_path = potential_thumbnail
                    break
            
            if os.path.isfile(file_path):
                # Get file stats
                stats = os.stat(file_path)
                
                # Extract video duration
                import cv2
                video = cv2.VideoCapture(file_path)
                fps = video.get(cv2.CAP_PROP_FPS)
                frame_count = int(video.get(cv2.CAP_PROP_FRAME_COUNT))
                duration = frame_count / fps if fps > 0 else 0
                video.release()
                
                # Format duration
                minutes = int(duration // 60)
                seconds = int(duration % 60)
                duration_str = f"{minutes}:{seconds:02d}"

                # Generate URLs
                if thumbnail_path:
                    # Convert thumbnail path to URL format
                    rel_thumbnail_path = os.path.relpath(thumbnail_path, OUTPUT_DIR)
                    thumbnail_url = f"/output/{rel_thumbnail_path.replace(os.sep, '/')}"
                else:
                    # Fallback to video thumbnail
                    thumbnail_url = f"/static/default-thumbnail.png"  # You should add a default thumbnail

                # Convert video path to URL format
                rel_video_path = os.path.relpath(file_path, OUTPUT_DIR)
                video_url = f"/output/{rel_video_path.replace(os.sep, '/')}"
                
                files.append({
                    'title': base_name,
                    'image': thumbnail_url,
                    'video_url': video_url,
                    'createdAt': datetime.fromtimestamp(stats.st_ctime).strftime('%Y.%m.%d %H:%M:%S'),
                    'duration': duration_str,
                    'size': stats.st_size,
                    'filename': video_filename  # Add original filename
                })
        
        # Sort files by creation date (newest first)
        files.sort(key=lambda x: x['createdAt'], reverse=True)
        
        return jsonify({
            'success': True,
            'files': files
        })
        
    except Exception as e:
        print(f"Error checking gallery: {str(e)}")
        return jsonify({'error': str(e)}), 500

@app.route('/download/<path:filename>')
def download_file(filename):
    try:
        # Ensure the file path is within OUTPUT_DIR
        full_path = os.path.join(OUTPUT_DIR, filename)
        return send_file(full_path, as_attachment=True)
    except Exception as e:
        return jsonify({'error': str(e)}), 404

@app.route('/get_latest_recommendation', methods=['GET'])
def get_latest_recommendation():
    try:
        user_id = request.args.get('userId')
        if not user_id:
            return jsonify({'error': 'User ID is required'}), 400

        # recommendations 디렉토리 경로
        recommendations_dir = os.path.join(OUTPUT_DIR, user_id, 'recommendations')
        
        if not os.path.exists(recommendations_dir):
            return jsonify({'error': 'No recommendations found'}), 404

        # JSON 파일들을 생성 시간 기준으로 정렬
        json_files = [f for f in os.listdir(recommendations_dir) if f.endswith('.json')]
        if not json_files:
            return jsonify({'error': 'No recommendation files found'}), 404

        latest_file = max(
            [os.path.join(recommendations_dir, f) for f in json_files],
            key=os.path.getctime
        )

        # 최신 파일 읽기
        with open(latest_file, 'r', encoding='utf-8') as f:
            recommendation_data = json.load(f)

        return jsonify({
            'success': True,
            'recommendation': recommendation_data
        })

    except Exception as e:
        print(f"Error in get_latest_recommendation: {str(e)}")
        return jsonify({'error': str(e)}), 500

@app.route('/get_latest_video', methods=['GET'])
def get_latest_video():
    try:
        user_id = request.args.get('userId')
        if not user_id:
            return jsonify({'error': 'User ID is required'}), 400

        # 비디오 디렉토리 경로
        videos_dir = os.path.join(OUTPUT_DIR, user_id, 'videos')
        
        if not os.path.exists(videos_dir):
            return jsonify({'error': 'No videos found'}), 404

        # MP4 파일들을 생성 시간 기준으로 정렬
        video_files = [f for f in os.listdir(videos_dir) if f.endswith('.mp4')]
        if not video_files:
            return jsonify({'error': 'No video files found'}), 404

        latest_video = max(
            [os.path.join(videos_dir, f) for f in video_files],
            key=os.path.getctime
        )

        # 응답 데이터 구성
        video_filename = os.path.basename(latest_video)
        folder = os.path.basename(os.path.dirname(latest_video))

        return jsonify({
            'success': True,
            'filename': video_filename,
            'folder': folder
        })

    except Exception as e:
        print(f"Error in get_latest_video: {str(e)}")
        return jsonify({'error': str(e)}), 500

# 파일 확장자 검증 함수
def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

# 이미지 업로드 라우트
@app.route('/upload_image', methods=['POST'])
def upload_image():
    """이미지 업로드 처리"""
    try:
        if 'image' not in request.files:
            return jsonify({'error': 'No image file provided'}), 400
            
        file = request.files['image']
        if file.filename == '':
            return jsonify({'error': 'No selected file'}), 400
            
        if not allowed_file(file.filename):
            return jsonify({'error': 'Invalid file type'}), 400

        # 사용자 ID와 서브 경로 가져오기
        user_id = request.form.get('userId', 'default')
        sub_path = request.form.get('subPath', 'images')
        
        # 사용자별 업로드 디렉토리 생성
        upload_folder = os.path.join(BASE_UPLOAD_FOLDER, user_id, sub_path)
        os.makedirs(upload_folder, exist_ok=True)
            
        # 파일명 안전하게 처리 및 타임스탬프 추가
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        filename = f"{timestamp}_{secure_filename(file.filename)}"
        save_path = os.path.join(upload_folder, filename)
        
        # 파일 저장
        file.save(save_path)
        
        return jsonify({
            'success': True,
            'path': save_path
        })

    except Exception as e:
        print(f"Error uploading image: {str(e)}")
        return jsonify({'error': str(e)}), 500

def calculate_dimensions(image_path: str, max_size: int) -> tuple[int, int]:
    """이미지 비율을 유지하면서 새로운 크기 계산"""
    with Image.open(image_path) as img:
        width, height = img.size
        
        if width >= height:
            new_width = max_size
            new_height = int((height * max_size) / width)
        else:
            new_height = max_size
            new_width = int((width * max_size) / height)
            
        # 32의 배수로 조정
        new_width = (new_width // 32) * 32
        new_height = (new_height // 32) * 32
        
        return new_width, new_height

@app.route('/generate_ltxv', methods=['POST'])
def generate_ltxv():
    """LTXV 비디오 생성"""
    try:
        data = request.json
        prompt = data.get('prompt')
        image_path = data.get('imagePath')
        use_random_seed = data.get('useRandomSeed')
        frame_length = data.get('frameLength', 65)
        max_size = data.get('width', 800)  # 최대 해상도로 사용
        folder_name = data.get('savePath', 'videos')
        fps = data.get('fps', 25)
        
        if not prompt or not image_path:
            return jsonify({'error': 'Prompt and image path are required'}), 400
        
        if not os.path.exists(image_path):
            return jsonify({'error': 'Image file not found'}), 400
            
        if use_random_seed:
            seed = random.randint(1, 999999999999999)
        else:
            try:
                seed = int(data.get('seed'))
                if not (1 <= seed <= 999999999999999):
                    return jsonify({'error': 'Seed must be between 1 and 999999999999999'}), 400
            except (TypeError, ValueError):
                return jsonify({'error': 'Invalid seed value'}), 400
        
        try:
            # 이미지 비율을 유지하며 크기 계산
            width, height = calculate_dimensions(image_path, max_size)
            print(f"Calculated dimensions: {width}x{height} from max_size: {max_size}")
            
            # ComfyUI 큐가 처리될 시간을 주기 위해 잠시 대기
            time.sleep(1)
            
            video_path = asyncio.run(ltxv_client.generate_video(
                image_path=image_path,
                prompt=prompt,
                folder_name=folder_name,
                base_filename="video",
                seed=seed,
                frame_length=frame_length,
                width=width,
                height=height,
                fps=fps
            ))
            
            # 비디오 생성이 완료될 때까지 대기
            while not os.path.exists(video_path):
                time.sleep(0.5)
                
            # 파일이 완전히 쓰여질 때까지 추가 대기
            time.sleep(2)
            
            filename = os.path.basename(video_path)
            folder = os.path.basename(os.path.dirname(video_path))
            return jsonify({
                'success': True,
                'seed': seed,
                'filename': filename,
                'folder': folder,
                'dimensions': {
                    'width': width,
                    'height': height
                }
            })
        except Exception as e:
            print(f"Error generating LTXV video: {str(e)}")
            return jsonify({'error': str(e)}), 500

    except Exception as e:
        print(f"Error in generate_ltxv: {str(e)}")
        return jsonify({'error': str(e)}), 500
if __name__ == '__main__':
    config = load_config()
    app.run(debug=True, host='0.0.0.0', port=8888)