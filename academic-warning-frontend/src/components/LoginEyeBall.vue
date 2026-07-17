<template>
  <div class="leb-eye" :style="eyeStyle">
    <LoginPupil v-if="!isBlinking"
      :size="pupilSize" :max-distance="maxDistance" :pupil-color="pupilColor"
      :force-look-x="forceLookX" :force-look-y="forceLookY"
      :mouse-x="mouseX" :mouse-y="mouseY" :ref-el="refEl" />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import LoginPupil from './LoginPupil.vue'

const props = defineProps({
  size: { type: Number, default: 48 },
  pupilSize: { type: Number, default: 16 },
  maxDistance: { type: Number, default: 10 },
  eyeColor: { type: String, default: 'white' },
  pupilColor: { type: String, default: '#2D2D2D' },
  isBlinking: { type: Boolean, default: false },
  forceLookX: { type: Number, default: undefined },
  forceLookY: { type: Number, default: undefined },
  mouseX: { type: Number, default: 0 },
  mouseY: { type: Number, default: 0 },
  refEl: { default: null },
})

const eyeStyle = computed(() => ({
  width: props.size + 'px',
  height: props.isBlinking ? '2px' : props.size + 'px',
  backgroundColor: props.eyeColor,
  borderRadius: '50%',
  overflow: 'hidden',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  transition: 'height 0.15s',
  flexShrink: 0,
}))
</script>

<style scoped>
.leb-eye { flex-shrink: 0; }
</style>
