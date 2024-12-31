import 'package:flutter_test/flutter_test.dart';
import 'package:sm_linktop/sm_linktop.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockSmLinktopPlatform
    with MockPlatformInterfaceMixin
     {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {

}
