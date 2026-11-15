# 타고보고

차에서 내려 고객을 방문하고 돌아온 기록과 업무 메모를 남기는 Pleos Connect 차량용 앱입니다.

현재 앱은 두 업무 유형(설치·AS, 영업)의 시연용 방문 사례를 보여줍니다. 설정에서 유형을 바꾸면 방문 후보, 메모 항목, 보고서 내용이 전환됩니다. 메모는 유형별로 기기에 저장되고 보고서 본문을 복사할 수 있습니다. 자동 방문 기록과 차량 신호는 아직 제품 화면에 연결되지 않았습니다.

방문처·시각·센서 상태는 시연용 고정 값입니다. 메모 항목은 처음 열 때 사례 값으로 채워지며, 이미 저장한 메모는 덮어쓰지 않습니다.

## 실행 방법

1. Android Studio에서 이 저장소를 열고 **Gradle JDK를 17**로 설정한 뒤 동기화합니다.
2. 아래의 Pleos Connect 에뮬레이터를 실행합니다.
3. Windows PowerShell에서도 `JAVA_HOME`이 JDK 17을 가리키도록 설정한 뒤 디버그 APK를 빌드하고 설치합니다.

   ```powershell
   .\gradlew.bat :app:assembleDebug
   adb devices
   adb install -r .\app\build\outputs\apk\debug\app-debug.apk
   ```

4. 에뮬레이터 런처에서 **타고보고**를 엽니다. SDK 검증 화면을 보려면 다음 명령을 실행합니다.

   ```powershell
   adb shell am start -n com.tagobogo/.VerifyActivity
   ```

macOS·Linux에서는 빌드 명령의 `.\gradlew.bat` 대신 `./gradlew`를 사용합니다.

## 테스트 환경 및 필요한 설정

| 항목 | 설정 |
|---|---|
| JDK | 17 |
| Android SDK Platform | 35 |
| Gradle / Android Gradle Plugin | 8.10.2 / 8.8.2 |
| 앱 SDK 설정 | `compileSdk` 35, `targetSdk` 35, `minSdk` 28 |
| 테스트 기기 | Pleos Connect 2.1.0 에뮬레이터 (Android API 34) |

Android Studio의 **SDK Update Sites**에 `https://nexus-playground.pleos.ai/repository/raw-releases/connect-avd/2.x/crp-sys-img.xml`을 추가하고, SDK Manager에서 Pleos Connect 2.1.0 시스템 이미지를 설치합니다. Device Manager에서 **Automotive → Pleos Connect v2.1.0** AVD를 생성합니다. 개발 환경에서는 메모리 4GB와 Cold boot를 사용했습니다. Pleos SDK 의존성 저장소는 [`settings.gradle.kts`](settings.gradle.kts)에 설정되어 있습니다.

차량 에뮬레이터 없이 방문 판정 로직의 JVM 테스트를 실행할 수 있습니다.

```powershell
.\gradlew.bat :core:derive:test
```

차량 신호 주입과 추가 검증 방법은 [환경설정.md](환경설정.md)를 참고하세요.

## 개발에 사용한 에뮬레이터와 화면 규격

| 항목 | 규격 |
|---|---|
| 에뮬레이터 | Pleos Connect 2.1.0 (Android API 34) |
| 화면 타입 | Connect-S-17 |
| 디스플레이 | 2560 × 1440 px, 160 dpi, 가로 방향 |
| 앱 창 (한 개) | 1700 × 1184 dp |
| 앱 창 (분할 시) | 840 × 1184 dp |

앱 창 크기는 전체 디스플레이에서 상태 표시줄, 드라이빙뷰, GNB 등을 제외한 영역입니다. 다른 화면 타입의 규격은 [환경설정.md](환경설정.md#화면-규격)에 정리되어 있습니다.

## APK 파일 위치

[apk/조하현_타고보고_release.apk](apk/조하현_타고보고_release.apk)