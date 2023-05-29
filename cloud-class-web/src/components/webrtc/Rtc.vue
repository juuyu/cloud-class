<template>
    <div>
      <h1>My Peer ID: {{ peerId }}</h1>
      <div v-if="showConnectForm">
        <form @submit.prevent="connect">
          <label for="remotePeerId">Remote Peer ID:</label>
          <input v-model="remotePeerId" id="remotePeerId" type="text" />
          <button>Connect</button>
        </form>
      </div>
      <div v-else>
        <video ref="localVideo" autoplay></video>
        <video ref="remoteVideo" autoplay></video>
        <button @click="disconnect">Disconnect</button>
      </div>
    </div>
  </template>
  
  <script lang="js">
  import { defineComponent } from 'vue'
  import SimplePeer from 'simple-peer'
  
  export default defineComponent({
    name: 'App',
    data() {
      return {
        peerId: '',
        remotePeerId: '',
        peer: null,
        showConnectForm: true,
      }
    },
    async created() {
      const stream = await navigator.mediaDevices.getUserMedia({ video: true, audio: true })
      this.$refs.localVideo.srcObject = stream
  
      // Generate random peer ID
      this.peerId = Math.random().toString(36).slice(2)
  
      // Create new Simple-Peer instance with our ID as initiator
      this.peer = new SimplePeer({
        initiator: true,
        stream,
      })
  
      // When we get a signal from the peer, send it to them
      this.peer.on('signal', signal => {
        console.log('My signal:', signal)
        this.peer.signal(signal)
      })
  
      // When we receive a stream from the peer, show it in the remote video element
      this.peer.on('stream', stream => {
        this.$refs.remoteVideo.srcObject = stream
      })
    },
    methods: {
      async connect() {
        // Create new Simple-Peer instance with remote peer ID
        this.peer = new SimplePeer()
  
        // When we get a signal from the peer, send it to them
        this.peer.on('signal', signal => {
          console.log('My signal:', signal)
          this.peer.signal(signal)
        })
  
        // When we receive a stream from the peer, show it in the remote video element
        this.peer.on('stream', stream => {
          this.$refs.remoteVideo.srcObject = stream
        })
  
        // When the peer's signal is received, send our signal to them
        this.peer.on('signal', signal => {
          console.log('Remote signal:', signal)
          this.peer.signal(signal)
        })
  
        // Connect to remote peer ID
        try {
          const signal = await this.peer.signal(this.remotePeerId)
          console.log('Connected to peer:', signal)
          this.showConnectForm = false
        } catch (err) {
          console.error('Error connecting to peer:', err)
          alert('Could not connect to remote peer.')
        }
      },
      disconnect() {
        this.peer.destroy()
        this.showConnectForm = true
      },
    },
  })
  </script>
  