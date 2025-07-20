<template>
  <div class="trade-registration-page">
    <div class="page-header">
      <h2>取引登録</h2>
      <button @click="$router.push('/trades')" class="back-button">
        取引一覧に戻る
      </button>
    </div>

    <div class="registration-form">
      <form @submit.prevent="saveTrade">
        <!-- 種別選択 -->
        <div class="form-section">
          <h3>基本情報</h3>
          <div class="form-group">
            <label>種別:</label>
            <select v-model="tradeForm.category" @change="onCategoryChange" required>
              <option value="">種別を選択</option>
              <option value="0">現金預金</option>
              <option value="1">国内株式</option>
              <option value="2">外国株式</option>
              <option value="3">日本国債</option>
              <option value="4">外国国債</option>
            </select>
          </div>

          <!-- 名称（オートコンプリート付き） -->
          <div class="form-group">
            <label>名称:</label>
            <div class="autocomplete-container">
              <input 
                v-model="tradeForm.name" 
                @input="onNameInput"
                @focus="showSuggestions = true"
                placeholder="名称を入力..."
                required
              />
              <div v-if="showSuggestions && filteredAssets.length > 0" class="suggestions">
                <div 
                  v-for="asset in filteredAssets" 
                  :key="asset.id"
                  @click="selectAsset(asset)"
                  class="suggestion-item"
                >
                  {{ asset.name }}
                  <span class="asset-category">{{ getCategoryName(asset.category) }}</span>
                </div>
              </div>
            </div>
          </div>

          <div class="form-group">
            <label>概要:</label>
            <textarea v-model="tradeForm.description" rows="3" placeholder="概要を入力..."></textarea>
          </div>
        </div>

        <!-- 金額情報 -->
        <div class="form-section">
          <h3>金額情報</h3>
          <div class="form-row">
            <div class="form-group">
              <label>取得額合計:</label>
              <input 
                v-model="tradeForm.totalAcquisition" 
                type="number" 
                step="0.01"
                placeholder="0.00"
                readonly
              />
              <small>現在の累計取得額（自動計算）</small>
            </div>
            <div class="form-group">
              <label>評価額合計:</label>
              <input 
                v-model="tradeForm.totalValuation" 
                type="number" 
                step="0.01"
                placeholder="0.00"
              />
            </div>
          </div>
        </div>

        <!-- 取引詳細 -->
        <div class="form-section">
          <h3>取引詳細</h3>
          <div class="form-row">
            <div class="form-group">
              <label>基準日:</label>
              <input v-model="tradeForm.navDate" type="date" required />
            </div>
            <div class="form-group">
              <label>基準額:</label>
              <input 
                v-model="tradeForm.nav" 
                type="number" 
                step="0.01"
                placeholder="0.00"
                required
              />
              <small>最新の基準額が自動設定されます</small>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>取引金額:</label>
              <input 
                v-model="tradeForm.tradeAmount" 
                type="number" 
                step="0.01"
                placeholder="0.00"
                required
              />
              <small>直前の取引金額が初期値として設定されます</small>
            </div>
            <div class="form-group">
              <label>口数:</label>
              <input 
                v-model="tradeForm.units" 
                type="number" 
                step="0.01"
                placeholder="0.00"
                required
              />
              <small>取引金額 ÷ 基準額で自動計算されます</small>
            </div>
          </div>

          <div class="form-group">
            <label>ステータス:</label>
            <select v-model="tradeForm.status" required>
              <option value="0">積立</option>
              <option value="1">保有のみ</option>
              <option value="2">売却</option>
            </select>
            <small>
              積立の場合、取引金額が累計額に加算されます。<br>
              売却の場合、取引金額が累計額から減額されます。
            </small>
          </div>
        </div>

        <!-- 計算結果プレビュー -->
        <div class="form-section preview-section">
          <h3>計算結果プレビュー</h3>
          <div class="preview-grid">
            <div class="preview-item">
              <label>現在の累計取得額:</label>
              <span>{{ formatCurrency(currentTotalAcquisition) }}</span>
            </div>
            <div class="preview-item">
              <label>今回の取引金額:</label>
              <span>{{ formatCurrency(tradeForm.tradeAmount) }}</span>
            </div>
            <div class="preview-item">
              <label>取引後の累計取得額:</label>
              <span :class="getTotalClass()">{{ formatCurrency(newTotalAcquisition) }}</span>
            </div>
            <div class="preview-item">
              <label>計算された口数:</label>
              <span>{{ calculatedUnits }}</span>
            </div>
          </div>
        </div>

        <!-- アクションボタン -->
        <div class="form-actions">
          <button type="button" @click="calculateUnits" class="calculate-button">
            口数計算
          </button>
          <button type="submit" class="save-button">
            保存
          </button>
          <button type="button" @click="$router.push('/trades')" class="cancel-button">
            キャンセル
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, watch } from 'vue'
import axios from 'axios'

export default {
  name: 'TradeRegistration',
  setup() {
    const tradeForm = ref({
      category: '',
      name: '',
      description: '',
      totalAcquisition: '0',
      totalValuation: '',
      navDate: '',
      nav: '',
      tradeAmount: '',
      units: '',
      status: '0'
    })

    const assets = ref([])
    const filteredAssets = ref([])
    const showSuggestions = ref(false)
    const selectedAssetId = ref(null)
    const currentTotalAcquisition = ref(0)

    // 種別変更時の処理
    const onCategoryChange = () => {
      tradeForm.value.name = ''
      selectedAssetId.value = null
      currentTotalAcquisition.value = 0
      tradeForm.value.totalAcquisition = '0'
    }

    // 名称入力時の処理
    const onNameInput = () => {
      if (tradeForm.value.name.length > 0) {
        filteredAssets.value = assets.value.filter(asset => 
          asset.name.toLowerCase().includes(tradeForm.value.name.toLowerCase()) &&
          asset.category === tradeForm.value.category
        )
        showSuggestions.value = true
      } else {
        showSuggestions.value = false
        filteredAssets.value = []
      }
    }

    // 資産選択時の処理
    const selectAsset = async (asset) => {
      tradeForm.value.name = asset.name
      tradeForm.value.description = asset.description || ''
      selectedAssetId.value = asset.id
      showSuggestions.value = false
      
      // 既存の取引情報を取得
      await loadAssetTradeInfo(asset.id)
    }

    // 資産の取引情報を読み込み
    const loadAssetTradeInfo = async (assetId) => {
      try {
        // 最新の基準額を取得
        const navResponse = await axios.get(`/api/trades/latest-nav/${assetId}`)
        if (navResponse.data.latestNav) {
          tradeForm.value.nav = navResponse.data.latestNav
        }
        
        // 直前の取引金額を取得
        const amountResponse = await axios.get(`/api/trades/previous-amount/${assetId}`)
        if (amountResponse.data.previousAmount) {
          tradeForm.value.tradeAmount = amountResponse.data.previousAmount
        }
        
        // 現在の累計取得額を取得
        const statisticsResponse = await axios.get(`/api/trades/statistics/${assetId}`)
        currentTotalAcquisition.value = statisticsResponse.data.totalBuyAmount || 0
        tradeForm.value.totalAcquisition = currentTotalAcquisition.value.toString()
        
      } catch (error) {
        console.error('資産情報取得エラー:', error)
      }
    }

    // 口数計算
    const calculateUnits = () => {
      if (tradeForm.value.tradeAmount && tradeForm.value.nav) {
        const tradeAmount = parseFloat(tradeForm.value.tradeAmount)
        const nav = parseFloat(tradeForm.value.nav)
        if (nav > 0) {
          tradeForm.value.units = (tradeAmount / nav).toFixed(4)
        }
      }
    }

    // 取引保存
    const saveTrade = async () => {
      try {
        // まず資産を作成または更新
        let assetId = selectedAssetId.value
        if (!assetId) {
          // 新しい資産を作成
          const assetData = {
            category: tradeForm.value.category,
            name: tradeForm.value.name,
            description: tradeForm.value.description,
            totalAcquisition: newTotalAcquisition.value.toString(),
            status: parseInt(tradeForm.value.status)
          }
          const assetResponse = await axios.post('/api/assets', assetData)
          assetId = assetResponse.data.id
        }

        // 取引を作成
        const tradeData = {
          securityId: assetId,
          nav: tradeForm.value.nav,
          tradeAmount: tradeForm.value.tradeAmount,
          units: tradeForm.value.units,
          navDate: tradeForm.value.navDate,
          tradeStatus: tradeForm.value.status === '2' ? '0' : '1' // 売却は0、それ以外は1
        }
        
        await axios.post('/api/trades', tradeData)
        
        // 成功時の処理
        alert('取引が正常に保存されました')
        window.location.href = '/trades'
        
      } catch (error) {
        console.error('取引保存エラー:', error)
        alert('取引の保存に失敗しました')
      }
    }

    // 計算値
    const calculatedUnits = computed(() => {
      if (tradeForm.value.tradeAmount && tradeForm.value.nav) {
        const tradeAmount = parseFloat(tradeForm.value.tradeAmount)
        const nav = parseFloat(tradeForm.value.nav)
        if (nav > 0) {
          return (tradeAmount / nav).toFixed(4)
        }
      }
      return '0.0000'
    })

    const newTotalAcquisition = computed(() => {
      const current = currentTotalAcquisition.value
      const tradeAmount = parseFloat(tradeForm.value.tradeAmount) || 0
      
      if (tradeForm.value.status === '0') { // 積立
        return current + tradeAmount
      } else if (tradeForm.value.status === '2') { // 売却
        return current - tradeAmount
      } else { // 保有のみ
        return current
      }
    })

    // ユーティリティ関数
    const getCategoryName = (category) => {
      const categories = {
        '0': '現金預金',
        '1': '国内株式',
        '2': '外国株式',
        '3': '日本国債',
        '4': '外国国債'
      }
      return categories[category] || '不明'
    }

    const formatCurrency = (amount) => {
      return new Intl.NumberFormat('ja-JP', {
        style: 'currency',
        currency: 'JPY'
      }).format(amount || 0)
    }

    const getTotalClass = () => {
      const current = currentTotalAcquisition.value
      const newTotal = newTotalAcquisition.value
      if (newTotal > current) return 'increase'
      if (newTotal < current) return 'decrease'
      return ''
    }

    // 資産データの読み込み
    const loadAssets = async () => {
      try {
        const response = await axios.get('/api/assets')
        assets.value = response.data
      } catch (error) {
        console.error('資産の読み込みエラー:', error)
      }
    }

    // 初期化
    onMounted(() => {
      loadAssets()
      // 今日の日付を設定
      tradeForm.value.navDate = new Date().toISOString().split('T')[0]
    })

    // 取引金額または基準額が変更されたら口数を自動計算
    watch([() => tradeForm.value.tradeAmount, () => tradeForm.value.nav], () => {
      calculateUnits()
    })

    return {
      tradeForm,
      assets,
      filteredAssets,
      showSuggestions,
      currentTotalAcquisition,
      onCategoryChange,
      onNameInput,
      selectAsset,
      calculateUnits,
      saveTrade,
      calculatedUnits,
      newTotalAcquisition,
      getCategoryName,
      formatCurrency,
      getTotalClass
    }
  }
}
</script>

<style scoped>
.trade-registration-page {
  max-width: 800px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.back-button {
  background: #6c757d;
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 6px;
  cursor: pointer;
  font-size: 1rem;
}

.registration-form {
  background: white;
  border-radius: 8px;
  padding: 2rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.form-section {
  margin-bottom: 2rem;
  padding-bottom: 1.5rem;
  border-bottom: 1px solid #eee;
}

.form-section:last-child {
  border-bottom: none;
}

.form-section h3 {
  color: #2c3e50;
  margin-bottom: 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 2px solid #3498db;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: bold;
  color: #2c3e50;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.form-group textarea {
  resize: vertical;
  min-height: 80px;
}

.form-group small {
  display: block;
  margin-top: 0.25rem;
  color: #666;
  font-size: 0.875rem;
}

.autocomplete-container {
  position: relative;
}

.suggestions {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border: 1px solid #ddd;
  border-top: none;
  border-radius: 0 0 4px 4px;
  max-height: 200px;
  overflow-y: auto;
  z-index: 1000;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.suggestion-item {
  padding: 0.75rem;
  cursor: pointer;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.suggestion-item:hover {
  background: #f8f9fa;
}

.suggestion-item:last-child {
  border-bottom: none;
}

.asset-category {
  font-size: 0.8rem;
  color: #666;
  background: #e9ecef;
  padding: 0.25rem 0.5rem;
  border-radius: 12px;
}

.preview-section {
  background: #f8f9fa;
  border-radius: 6px;
  padding: 1.5rem;
}

.preview-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1rem;
}

.preview-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem;
  background: white;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.preview-item label {
  font-weight: bold;
  color: #495057;
}

.preview-item span {
  font-size: 1.1rem;
  font-weight: bold;
}

.increase {
  color: #28a745;
}

.decrease {
  color: #dc3545;
}

.form-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  margin-top: 2rem;
  padding-top: 2rem;
  border-top: 1px solid #eee;
}

.calculate-button,
.save-button,
.cancel-button {
  padding: 0.75rem 2rem;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: bold;
}

.calculate-button {
  background: #17a2b8;
  color: white;
}

.save-button {
  background: #28a745;
  color: white;
}

.cancel-button {
  background: #6c757d;
  color: white;
}

.calculate-button:hover,
.save-button:hover,
.cancel-button:hover {
  opacity: 0.9;
}

@media (max-width: 768px) {
  .form-row {
    grid-template-columns: 1fr;
  }
  
  .preview-grid {
    grid-template-columns: 1fr;
  }
  
  .form-actions {
    flex-direction: column;
  }
}
</style> 