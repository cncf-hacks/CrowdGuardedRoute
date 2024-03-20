import pickle
import numpy as np

from tensorflow import keras

BATCH_SIZE = 2048

# import model
pretrained_model = keras.models.load_model('./model/ScoreModel.keras')

# import scaler
with open('./model/ModelScaler.pkl', 'rb') as f:
  scaler = pickle.load(f)

def preprocessing(input_ds):

    input_ds = input_ds.copy()

    # 1. Illuminace 열의 range가 너무 커 log scale로 변환
    eps = 0.001
    input_ds['Log Illuminance'] = np.log(input_ds.pop('illuminance') + eps)

    # 2. Scale 적용을 위한 WalkOrRun 분리
    input_wr = np.array(input_ds.pop('walkorrun'))

    # 3. Scale 적용
    input_features = np.array(input_ds)
    input_features = scaler.transform(input_features)
    input_features = np.clip(input_features, -5, 5)

    # 4. WalkOrRun 병합
    input_features = np.insert(input_features, 0, input_wr, axis=1)

    return input_features

def processML(input_features):
    pred_result = pretrained_model.predict(input_features, batch_size=BATCH_SIZE)
    return pred_result
