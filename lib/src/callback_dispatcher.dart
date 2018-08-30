import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

callbackDispatcher() {
  const MethodChannel _backgroundChannel =
      MethodChannel('dexterous.com/flutter/local_notifications_background');
  WidgetsFlutterBinding.ensureInitialized();

  _backgroundChannel.setMethodCallHandler((MethodCall call) async {
    print("Callback Dispatcher Invoked: ${call.arguments}");
    final args = call.arguments;
    final Function callback = PluginUtilities.getCallbackFromHandle(
        CallbackHandle.fromRawHandle(args[0]));
    assert(callback != null);
    callback();
  });
  print('notification dispatcher started');
  _backgroundChannel.invokeMethod('NotificationService.initialized');
}
