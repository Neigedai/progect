<template>
  <div class="floating-menu" v-if="items.length > 0">
    <div
      v-for="item in items"
      :key="item.id"
      class="floating-menu-item"
      :class="{ 'is-active': activeId === item.id }"
      @mouseenter="onEnter(item)"
      @mouseleave="onLeave"
    >
      <img v-if="item.icon" class="floating-menu-icon-img" :src="item.icon" :alt="item.title" />
      <el-icon v-else class="floating-menu-icon"><component :is="'QuestionFilled'" /></el-icon>
      <span class="floating-menu-label">{{ item.title }}</span>
      <transition name="fade">
        <div class="floating-menu-popup" v-show="activeId === item.id">
          <img :src="item.imageUrl" :alt="item.title" />
        </div>
      </transition>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getFloatingMenuItems } from '@/api/service'

const items = ref([])
const activeId = ref(null)

onMounted(async () => {
  try {
    const res = await getFloatingMenuItems()
    items.value = res.data || []
  } catch {
    // silent fail
  }
})

const onEnter = (item) => {
  activeId.value = item.id
}

const onLeave = () => {
  activeId.value = null
}
</script>

<style scoped>
.floating-menu {
  position: fixed;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  z-index: 100;
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.floating-menu-item {
  position: relative;
  width: 70px;
  height: 70px;
  background: #fff;
  color: #151515;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 2px;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 8px 0 0 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  border: 1px solid #e8e8e8;
  border-right: none;
}
.floating-menu-item.is-active {
  background: var(--primary);
  color: #fff;
  border-color: var(--primary);
  border-radius: 8px 0 0 8px;
  box-shadow: 0 4px 12px rgba(22, 93, 255, 0.35);
}
.floating-menu-icon {
  font-size: 18px;
}
.floating-menu-icon-img {
  width: 22px;
  height: 22px;
  object-fit: contain;
  filter: brightness(0) saturate(100%);
  transition: filter 0.25s;
}
.floating-menu-item.is-active .floating-menu-icon-img {
  filter: brightness(0) saturate(100%) invert(1);
}
.floating-menu-label {
  font-size: 11px;
  letter-spacing: 1px;
  line-height: 1;
}
.floating-menu-popup {
  position: absolute;
  right: 68px;
  top: 50%;
  transform: translateY(-50%);
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  padding: 8px;
}
.floating-menu-popup::after {
  content: '';
  position: absolute;
  right: -6px;
  top: 50%;
  transform: translateY(-50%);
  border: 4px solid transparent;
  border-left-color: #fff;
}
.floating-menu-popup img {
  max-width: 140px;
  max-height: 140px;
  display: block;
  border-radius: 4px;
  object-fit: contain;
}
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.15s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
@media (max-width: 768px) {
  .floating-menu-item {
    width: 38px;
    height: 38px;
    border-radius: 6px 0 0 6px;
  }
  .floating-menu-item.is-active {
    border-radius: 6px 0 0 6px;
  }
  .floating-menu-icon {
    font-size: 14px;
  }
  .floating-menu-icon-img {
    width: 16px;
    height: 16px;
  }
  .floating-menu-label {
    font-size: 9px;
  }
  .floating-menu-popup {
    right: 54px;
  }
  .floating-menu-popup img {
    max-width: 100px;
    max-height: 100px;
  }
}
</style>
