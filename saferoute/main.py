from flask import Flask, request
from ml import processML, preprocessing
import pandas as pd
import json
from io import StringIO

app = Flask(__name__)

dataColNames = ['walkorrun', 'illuminance', 'people', 'd2ps', 'd2fs', 'd2tl', 'd2l']
returnColNames = ['lat', 'lon']


@app.route('/post_json', methods=['POST'])
def process_json():
    try:
        content_type = request.headers.get('Content-Type')
        if (content_type == 'application/json'):
            dict_str = json.loads(request.data)
            feature_list = dict_str['list']

            result_array = list(map(process_data, feature_list))
            result = {'result': result_array}
            return result
        else:
            return 'Content-Type not supported!'
    except:
        result = {'result' : 'invalid content'}
        return result

def process_data(input_data):
    # Make dataframes
    #origin_df = pd.read_json(json.dumps(input_data))
    origin_df = pd.read_json(StringIO(json.dumps(input_data)))
    # slice dataframe and make ml data
    ml_df = origin_df[dataColNames]

    # preprocessing
    ml_data = preprocessing(ml_df)

    # get score
    scores = processML(ml_data)

    # assemble result
    result_df = origin_df[returnColNames].copy()
    result_df['score'] = scores
    result = json.loads(result_df.to_json(orient='records'))

    return result


if __name__ == "__main__":
    app.run('0.0.0.0', port=8080)
