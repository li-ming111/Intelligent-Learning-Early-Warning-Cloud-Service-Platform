<template>
  <div class="lp-pupil" :style="pupilStyle" />
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  size: { type: Number, default: 12 },
  maxDistance: { type: Number, default: 5 },
  pupilColor: { type: String, default: '#2D2D2D' },
  forceLookX: { type: Number, default: undefined },
  forceLookY: { type: Number, default: undefined },
  mouseX: { type: Number, default: 0 },
  mouseY: { type: Number, default: 0 },
  refEl: { default: null },
})

const position = computed(() => {
  if (!props.refEl) return { x: 0, y: 0 }
  const el = props.refEl
  if (props.forceLookX !== undefined && props.forceLookY !== undefined) {
    return { x: props.forceLookX, y: props.forceLookY }
  }
  // 获取元素相对容器的位置
  const rect = el.getBoundingClientRect()
  const cx = rect.left + rect.width / 2
  const cy = rect.top + rect.height / 2
  const dx = props.mouseX - cx
  const dy = props.mouseY - cy
  const dist = Math.min(Math.sqrt(dx * dx + dy * dy), props.maxDistance)
  const angle = Math.atan2(dy, dx)
  return { x: Math.cos(angle) * dist, y: Math.sin(angle) * dist }
})

const pupilStyle = computed(() => ({
  width: props.size + 'px',
  height: props.size + 'px',
  backgroundColor: props.pupilColor,
  borderRadius: '50%',
  transform: `translate(${position.value.x}px, ${position.value.y}px)`,
  transition: 'transform 0.1s ease-out',
}))
</script>

<style scoped>
.lp-pupil { flex-shrink: 0; }
</style>
