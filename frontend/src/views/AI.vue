<template>
  <div class="ai-page">
    <h2>AI連携機能</h2>
    <p>このページではAIサービスとの連携機能を提供します。</p>
    
    <div class="ai-interface">
      <div class="input-section">
        <h3>AIへの質問</h3>
        <textarea 
          v-model="userInput" 
          placeholder="AIに質問を入力してください..."
          rows="4"
          class="input-field"
        ></textarea>
        <button @click="sendToAI" :disabled="loading" class="send-button">
          {{ loading ? '処理中...' : 'AIに送信' }}
        </button>
      </div>
      
      <div class="response-section" v-if="aiResponse">
        <h3>AIの回答</h3>
        <div class="response-content">
          {{ aiResponse }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import axios from 'axios'

export default {
  name: 'AI',
  setup() {
    const userInput = ref('')
    const aiResponse = ref('')
    const loading = ref(false)

    const sendToAI = async () => {
      if (!userInput.value.trim()) return
      
      loading.value = true
      try {
        const response = await axios.post('/api/ai/process', {
          message: userInput.value
        })
        aiResponse.value = response.data.response
      } catch (error) {
        console.error('AI処理エラー:', error)
        aiResponse.value = 'エラーが発生しました。しばらく時間をおいて再度お試しください。'
      } finally {
        loading.value = false
      }
    }

    return {
      userInput,
      aiResponse,
      loading,
      sendToAI
    }
  }
}
</script>

<style scoped>
.ai-page {
  max-width: 800px;
  margin: 0 auto;
}

.ai-page h2 {
  color: #2c3e50;
  margin-bottom: 1rem;
}

.ai-interface {
  background: white;
  border-radius: 8px;
  padding: 2rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  margin-top: 2rem;
}

.input-section {
  margin-bottom: 2rem;
}

.input-section h3 {
  color: #2c3e50;
  margin-bottom: 1rem;
}

.input-field {
  width: 100%;
  padding: 1rem;
  border: 2px solid #e1e8ed;
  border-radius: 8px;
  font-size: 1rem;
  resize: vertical;
  margin-bottom: 1rem;
}

.input-field:focus {
  outline: none;
  border-color: #3498db;
}

.send-button {
  background: #3498db;
  color: white;
  border: none;
  padding: 0.75rem 2rem;
  border-radius: 6px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.3s;
}

.send-button:hover:not(:disabled) {
  background: #2980b9;
}

.send-button:disabled {
  background: #bdc3c7;
  cursor: not-allowed;
}

.response-section h3 {
  color: #2c3e50;
  margin-bottom: 1rem;
}

.response-content {
  background: #f8f9fa;
  padding: 1.5rem;
  border-radius: 8px;
  border-left: 4px solid #3498db;
  line-height: 1.6;
}
</style> 