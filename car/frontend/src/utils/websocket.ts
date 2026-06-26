/**
 * WebSocket 客户端工具
 * 支持自动重连、消息订阅
 */
export class WebSocketClient {
  private ws: WebSocket | null = null
  private url: string
  private reconnectTimer: number | null = null
  private listeners: Map<string, Set<(data: any) => void>> = new Map()

  constructor(url: string) {
    this.url = url
  }

  connect() {
    if (this.ws?.readyState === WebSocket.OPEN) return

    this.ws = new WebSocket(this.url)

    this.ws.onopen = () => {
      console.log('WebSocket连接建立:', this.url)
      this.emit('_connected', {})
    }

    this.ws.onmessage = (event) => {
      try {
        const msg = JSON.parse(event.data)
        if (msg.type) {
          this.emit(msg.type, msg.data || msg)
        }
      } catch (e) {
        console.warn('WebSocket消息解析失败:', event.data)
      }
    }

    this.ws.onclose = () => {
      console.log('WebSocket连接关闭')
      this.emit('_disconnected', {})
      this.scheduleReconnect()
    }

    this.ws.onerror = (error) => {
      console.error('WebSocket错误:', error)
    }
  }

  send(data: string | object) {
    if (this.ws?.readyState === WebSocket.OPEN) {
      this.ws.send(typeof data === 'string' ? data : JSON.stringify(data))
    }
  }

  subscribe(vehicleId: number) {
    this.send(`subscribe:${vehicleId}`)
  }

  unsubscribe() {
    this.send('unsubscribe')
  }

  on(event: string, callback: (data: any) => void) {
    if (!this.listeners.has(event)) {
      this.listeners.set(event, new Set())
    }
    this.listeners.get(event)!.add(callback)
  }

  off(event: string, callback: (data: any) => void) {
    this.listeners.get(event)?.delete(callback)
  }

  private emit(event: string, data: any) {
    this.listeners.get(event)?.forEach(cb => cb(data))
  }

  private scheduleReconnect() {
    if (this.reconnectTimer) return
    this.reconnectTimer = window.setTimeout(() => {
      this.reconnectTimer = null
      this.connect()
    }, 5000)
  }

  disconnect() {
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer)
      this.reconnectTimer = null
    }
    this.ws?.close()
    this.ws = null
  }
}
