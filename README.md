# Vision | NightCapture Lab 0.1

**Diagnóstico pessoal e offline para o Redmi 15C (25078RA3EL).** O objetivo do projeto é medir o que o hardware realmente consegue antes de criar o futuro gravador noturno.

## Recursos do protótipo
- Inventário Camera2 das câmeras, nível de hardware, ISO, exposição, FPS, estabilização, formatos e extensões.
- Diagnóstico dos codecs AVC/HEVC, resolução e recursos gráficos anunciados.
- Prévia, fotos JPEG e vídeo MP4 H.264/H.265 sem áudio, com 720p ou 1080p de teste.
- Perfis Automático, Detalhes, Movimento, Low Light Boost (se suportado) e Manual (se permitido).
- Amostras por quadro com ISO, exposição, sensor timestamp e FPS estimado.
- Exportação JSON e CSV para revisão posterior. Fotos e vídeos salvos localmente via MediaStore.

Sem internet, conta, microfone, GPS, anúncios, telemetria ou gravação oculta.

## Obter o APK
1. Abra [Actions](https://github.com/vitorsantana1574/Vision/actions).
2. Selecione **Vision Lab - build APK**; o push já deve disparar uma execução, ou use **Run workflow**.
3. Somente após a execução **verde**, abra o artifact `VisionLab-debug-APK`.
4. Baixe o ZIP e extraia `app-debug.apk`. Instale no Xiaomi e autorize a câmera.
5. Execute [PROTOCOLO_DE_TESTES.md](PROTOCOLO_DE_TESTES.md) e exporte JSON/CSV.

**Estado:** esta versão ainda necessita de verificação de build e testes reais no aparelho. Ela **não** inclui o processamento noturno futuro, nem promete revelar detalhes sem luz. Os modos podem ser ignorados ou limitados pelo Android/HyperOS.

**Compilação:** AGP 8.7.3, Gradle 8.9, Java 17, Android SDK 35. APK debug usa chave de assinatura temporária; atualizações definitivas exigirão uma chave privada de release estável.

## Referências
- https://www.mi.com/global/product/redmi-15c/specs/
- https://developer.android.com/reference/android/hardware/camera2/CameraCharacteristics
- https://developer.android.com/media/camera/lowlight/low-light-boost-ae
- https://developer.android.com/reference/android/media/MediaRecorder
