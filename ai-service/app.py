from flask import Flask, request, jsonify
from flask_cors import CORS
import os
from dotenv import load_dotenv
import openai

# 環境変数を読み込み
load_dotenv()

app = Flask(__name__)
CORS(app)

# OpenAI APIキーの設定
openai.api_key = os.getenv('OPENAI_API_KEY', 'your-api-key-here')

@app.route('/health', methods=['GET'])
def health_check():
    """ヘルスチェックエンドポイント"""
    return jsonify({
        'status': 'healthy',
        'service': 'AI Service',
        'version': '1.0.0'
    })

@app.route('/process', methods=['POST'])
def process_message():
    """AIメッセージ処理エンドポイント"""
    try:
        data = request.get_json()
        message = data.get('message', '')
        
        if not message:
            return jsonify({'error': 'メッセージが提供されていません'}), 400
        
        # OpenAI GPTを使用してレスポンスを生成
        response = generate_ai_response(message)
        
        return jsonify({
            'response': response,
            'status': 'success'
        })
        
    except Exception as e:
        return jsonify({
            'error': f'処理中にエラーが発生しました: {str(e)}',
            'status': 'error'
        }), 500

def generate_ai_response(message):
    """OpenAI GPTを使用してレスポンスを生成"""
    try:
        # 開発環境ではモックレスポンスを返す
        if not openai.api_key or openai.api_key == 'your-api-key-here':
            return f"開発モード: あなたのメッセージ「{message}」を受け取りました。実際のGPT APIキーを設定すると、より詳細な回答が得られます。"
        
        # OpenAI APIを使用してレスポンスを生成
        response = openai.ChatCompletion.create(
            model="gpt-3.5-turbo",
            messages=[
                {"role": "system", "content": "あなたは親切で役立つAIアシスタントです。日本語で回答してください。"},
                {"role": "user", "content": message}
            ],
            max_tokens=500,
            temperature=0.7
        )
        
        return response.choices[0].message.content
        
    except Exception as e:
        return f"AI処理中にエラーが発生しました: {str(e)}"

@app.route('/', methods=['GET'])
def index():
    """ルートエンドポイント"""
    return jsonify({
        'message': 'AIsa AI Service is running',
        'endpoints': {
            'health': '/health',
            'process': '/process'
        }
    })

if __name__ == '__main__':
    debug = os.getenv('FLASK_ENV') == 'development'
    app.run(host='0.0.0.0', port=5000, debug=debug) 