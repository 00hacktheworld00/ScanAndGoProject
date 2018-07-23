package com.sadashivsinha.scanandgo.Observer;

import android.arch.lifecycle.GeneratedAdapter;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MethodCallsLogger;
import java.lang.Override;

public class MyLifeCycleObserver_LifecycleAdapter implements GeneratedAdapter {
  final MyLifeCycleObserver mReceiver;

  MyLifeCycleObserver_LifecycleAdapter(MyLifeCycleObserver receiver) {
    this.mReceiver = receiver;
  }

  @Override
  public void callMethods(LifecycleOwner owner, Lifecycle.Event event, boolean onAny,
      MethodCallsLogger logger) {
    boolean hasLogger = logger != null;
    if (onAny) {
      if (!hasLogger || logger.approveCall("any", 1)) {
        mReceiver.any();
      }
      return;
    }
    if (event == Lifecycle.Event.ON_CREATE) {
      if (!hasLogger || logger.approveCall("create", 1)) {
        mReceiver.create();
      }
      return;
    }
    if (event == Lifecycle.Event.ON_START) {
      if (!hasLogger || logger.approveCall("start", 1)) {
        mReceiver.start();
      }
      return;
    }
    if (event == Lifecycle.Event.ON_RESUME) {
      if (!hasLogger || logger.approveCall("resume", 1)) {
        mReceiver.resume();
      }
      return;
    }
    if (event == Lifecycle.Event.ON_PAUSE) {
      if (!hasLogger || logger.approveCall("pause", 1)) {
        mReceiver.pause();
      }
      return;
    }
    if (event == Lifecycle.Event.ON_STOP) {
      if (!hasLogger || logger.approveCall("stop", 1)) {
        mReceiver.stop();
      }
      return;
    }
    if (event == Lifecycle.Event.ON_DESTROY) {
      if (!hasLogger || logger.approveCall("destroy", 1)) {
        mReceiver.destroy();
      }
      return;
    }
  }
}
