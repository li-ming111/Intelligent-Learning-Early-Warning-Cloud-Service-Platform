<template>
  <div class="test-page">
    <h1>测试页面</h1>
    <p>这是一个测试页面，用于验证Vue应用是否正常工作。</p>
    <button @click="handleClick">点击测试</button>
    <p>{{ message }}</p>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const message = ref('Hello Vue!')

const handleClick = () => {
  message.value = '按钮被点击了!'
  console.log('按钮点击成功')
}
</script>

<style scoped>
.test-page {
  padding: 40px;
  text-align: center;
}

button {
  padding: 10px 20px;
  background-color: #667eea;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background-color: #5568d3;
}
</style>
