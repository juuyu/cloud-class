/*
 *  Copyright (c) 2015 The WebRTC project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree.
 */

"use strict";

// Meter class that generates a number correlated to audio volume.
// The meter class itself displays nothing, but it makes the
// instantaneous and time-decaying volumes available for inspection.
// It also reports on the fraction of samples that were at or near
// the top of the measurement range.

export default class SoundMeter {
  mic: MediaStreamAudioSourceNode | undefined;
  script: ScriptProcessorNode;
  context: AudioContext;
  constructor(context: AudioContext, onAudioProcess: Function) {
    this.context = context;
    this.script = context.createScriptProcessor(2048, 1, 1);
    this.script.onaudioprocess = function (event) {
      let input = event.inputBuffer.getChannelData(0) || 0;
      let sum = 0.0;
      for (let i = 0; i < input.length; ++i) {
        sum += input[i] * input[i];
      }
      onAudioProcess && onAudioProcess(Math.sqrt(sum / input.length));
    };
  }

  connectToSource(stream: MediaStream) {
    this.mic = this.context.createMediaStreamSource(stream);
    this.mic.connect(this.script);
    // necessary to make sample run, but should not be.
    this.script.connect(this.context.destination);
  }

  stop() {
    this.mic && this.mic.disconnect();
    this.script && this.script.disconnect();
  }
}
