import tensorflow as tf
import joblib
import numpy as np
import pandas as pd

class AdRecommender:
    def __init__(self):
        # 모델 및 전처리기 로드
        self.model_time = tf.keras.models.load_model("model_time.h5")
        self.model_adtype = tf.keras.models.load_model("model_adtype.h5")
        self.scaler = joblib.load("scaler.pkl")
        self.label_encoders = joblib.load("label_encoders.pkl")

        # 필드명 매핑 정의
        self.field_mapping = {
            'gender': 'gender',
            'ageGroup': 'agegroup',
            'productCategory': 'product'
        }

        # 24시간 형식의 시간대 매핑
        self.time_mapping = {
            i: f"{i:02d}:00" for i in range(24)
        }

        # 입력값 변환을 위한 매핑 정의
        self.conversion_map = {
            'gender': {
                'male': 'Male',
                'female': 'Female',
            },
            'ageGroup': {
                'teens': '10대',
                'twenties': '20대',
                'thirties': '30대',
                'forties': '40대',
                'fifties': '50대',
                'sixties': '60대 이상',
                'seventyPlus': '60대 이상'
            },
            'productCategory': {
                'allCategory': '전자제품',
                'electronics': '전자제품',
                'fashion': '패션',
                'beauty': '뷰티&헬스',
                'food': '식품&식자재',
                'home&furniture': '인테리어가구',
                'sports': '스포츠아웃도어',
                'IT': 'IT',
                'books': '도서',
                'dailyNecessities': '생활용품'
            }
        }

    def _convert_input_values(self, input_data):
        """JSON 설정값을 모델 입력값으로 변환"""
        converted_input = {}
        try:
            for key, value in input_data.items():
                if key in self.conversion_map:
                    # 값 변환
                    converted_value = self.conversion_map[key].get(value, value)
                    # 필드명 매핑 적용
                    model_field = self.field_mapping[key]
                    converted_input[model_field] = converted_value
                    print(f"Converting {key}({model_field}): {value} -> {converted_value}")
        except Exception as e:
            print(f"Error in conversion: {str(e)}")
            raise
            
        return converted_input

    def predict(self, input_data):
        """광고 시간대와 유형 예측"""
        try:
            # 입력값 변환
            converted_input = self._convert_input_values(input_data)
            print(f"Converted input data: {converted_input}")
            
            # 모델 입력을 위한 변환
            model_fields = ["gender", "agegroup", "product"]
            encoded_input = []
            
            for field in model_fields:
                value = converted_input[field]
                encoder = self.label_encoders[field]
                encoded_value = encoder.transform([value])[0]
                encoded_input.append(encoded_value)
            
            input_df = pd.DataFrame(
                [encoded_input], 
                columns=model_fields
            )
            
            # 데이터 스케일링
            scaled_input = self.scaler.transform(input_df)

            # 모델 예측
            predicted_time = self.model_time.predict(scaled_input)
            predicted_adtype = self.model_adtype.predict(scaled_input)

            # 예측 결과 디코딩
            time_index = int(predicted_time.argmax(axis=1)[0])  # 0-23 범위의 시간
            adtype_index = int(predicted_adtype.argmax(axis=1)[0])

            # 시간대 매핑 적용
            decoded_time = self.time_mapping.get(time_index, "알 수 없음")
            decoded_adtype = self.label_encoders["adtype"].inverse_transform([adtype_index])[0]

            return {
                "recommended_time": decoded_time,
                "recommended_adtype": decoded_adtype
            }
            
        except Exception as e:
            print(f"Error in prediction: {str(e)}")
            raise

    def format_recommendation(self, recommendation):
        """추천 결과를 문자열로 포맷팅"""
        return f"[광고 추천] 최적 시간대: {recommendation['recommended_time']}, 추천 광고 유형: {recommendation['recommended_adtype']}\n\n"