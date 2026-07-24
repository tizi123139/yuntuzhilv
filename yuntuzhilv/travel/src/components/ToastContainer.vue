<template>
  <div class="toast-container">
    <TransitionGroup name="toast">
      <div
        v-for="item in toasts"
        :key="item.id"
        :class="['toast-item', `toast-${item.type}`]"
      >
        <!-- 图标 -->
        <div class="toast-icon">
          <!-- success -->
          <svg v-if="item.type === 'success'" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
            <polyline points="20 6 9 17 4 12" />
          </svg>
          <!-- error -->
          <svg v-else-if="item.type === 'error'" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
            <line x1="18" y1="6" x2="6" y2="18" />
            <line x1="6" y1="6" x2="18" y2="18" />
          </svg>
          <!-- warning -->
          <svg v-else-if="item.type === 'warning'" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
            <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z" />
            <line x1="12" y1="9" x2="12" y2="13" />
            <line x1="12" y1="17" x2="12.01" y2="17" />
          </svg>
          <!-- info -->
          <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="10" />
            <line x1="12" y1="16" x2="12" y2="12" />
            <line x1="12" y1="8" x2="12.01" y2="8" />
          </svg>
        </div>

        <!-- 消息文本 -->
        <span class="toast-message">{{ item.message }}</span>

        <!-- 关闭按钮 -->
        <button class="toast-close" @click="removeToast(item.id)">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round">
            <line x1="18" y1="6" x2="6" y2="18" />
            <line x1="6" y1="6" x2="18" y2="18" />
          </svg>
        </button>
      </div>
    </TransitionGroup>
  </div>
</template>

<script setup>
import { toasts } from '../utils/toast'

function removeToast(id) {
  toasts.value = toasts.value.filter(t => t.id !== id)
}
</script>

<style scoped>
.toast-container {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 99999;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  pointer-events: none;
  width: 100%;
  max-width: 420px;
  padding: 0 16px;
  box-sizing: border-box;
}

.toast-item {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  padding: 14px 16px;
  border-radius: 12px;
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  box-shadow:
    0 8px 32px rgba(0, 0, 0, 0.08),
    0 2px 8px rgba(0, 0, 0, 0.04);
  pointer-events: auto;
  font-size: 14px;
  line-height: 1.5;
  border: 1px solid rgba(255, 255, 255, 0.6);
}

/* 类型颜色 */
.toast-success {
  background: rgba(240, 253, 244, 0.95);
  color: #166534;
  border-color: rgba(34, 197, 94, 0.2);
}
.toast-success .toast-icon {
  color: #16a34a;
}

.toast-error {
  background: rgba(254, 242, 242, 0.95);
  color: #991b1b;
  border-color: rgba(239, 68, 68, 0.2);
}
.toast-error .toast-icon {
  color: #dc2626;
}

.toast-warning {
  background: rgba(255, 251, 235, 0.95);
  color: #92400e;
  border-color: rgba(245, 158, 11, 0.2);
}
.toast-warning .toast-icon {
  color: #d97706;
}

.toast-info {
  background: rgba(239, 246, 255, 0.95);
  color: #1e40af;
  border-color: rgba(59, 130, 246, 0.2);
}
.toast-info .toast-icon {
  color: #2563eb;
}

.toast-icon {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border-radius: 8px;
}

.toast-success .toast-icon {
  background: rgba(34, 197, 94, 0.12);
}
.toast-error .toast-icon {
  background: rgba(239, 68, 68, 0.12);
}
.toast-warning .toast-icon {
  background: rgba(245, 158, 11, 0.12);
}
.toast-info .toast-icon {
  background: rgba(59, 130, 246, 0.12);
}

.toast-message {
  flex: 1;
  font-weight: 500;
}

.toast-close {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border: none;
  background: rgba(0, 0, 0, 0.05);
  border-radius: 6px;
  cursor: pointer;
  color: inherit;
  opacity: 0.5;
  transition: opacity 0.2s, background 0.2s;
}
.toast-close:hover {
  opacity: 1;
  background: rgba(0, 0, 0, 0.1);
}

/* 滑入/滑出动画 */
.toast-enter-active {
  animation: toast-in 0.35s cubic-bezier(0.21, 1.02, 0.73, 1);
}
.toast-leave-active {
  animation: toast-out 0.25s cubic-bezier(0.06, 0.71, 0.26, 1) forwards;
}

@keyframes toast-in {
  0% {
    opacity: 0;
    transform: translateY(-20px) scale(0.95);
  }
  100% {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes toast-out {
  0% {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
  100% {
    opacity: 0;
    transform: translateY(-16px) scale(0.95);
  }
}

/* 多条时中间项的移动过渡 */
.toast-move {
  transition: transform 0.25s ease;
}

@media (max-width: 480px) {
  .toast-container {
    max-width: 100%;
    top: 12px;
    padding: 0 12px;
  }
  .toast-item {
    padding: 12px 14px;
    font-size: 13px;
  }
}
</style>
