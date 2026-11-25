# Vision Lab 0.4 | Diagnóstico dinâmico para Redmi 15C

Aplicativo pessoal, **offline**, para avaliar **vídeos noturnos com o Redmi 15C em movimento**. O objetivo é decidir o melhor sensor FPS/ISO/exposição/resolução/codec antes de desenvolver o processamento de vídeo do Vision.

## O que está implementado
- Câmera **traseira** via Camera2, prévia e captura JPEG.
- MP4 **sem áudio** com seletor **720p / 1080p**, **H.264 / HEVC**, **10 / 15 / 20 / 24 / 30 FPS**.
- Telemetria por vídeo: nome, versão do aplicativo, configuração solicitada, ISO, exposição, FPS do sensor, estados de AE/AF/AWB, distância de foco se reportada, contagem observada de quadros e sensor de movimento (giroscópio/acelerômetro quando disponíveis).
- Perfis **Automático** e **Manual** (ISO/exposição; limita exposição ao tempo por quadro em vídeo).
- Indicador de ISO, exposição e FPS estimado durante captura.
- Registro da configuração solicitada ao iniciar cada vídeo, duração, estado térmico inicial/final, amostras de foco/AE/AF e movimento em JSON; CSV inclui campos de foco e identificação de gravação.
- Fotos e vídeos na Galeria em `Pictures/VisionLab` e `Movies/VisionLab`.
- Diagnóstico com limites de ISO/exposição, tamanhos, codecs e suporte informado pela Camera2.

**Não implementado:** processamento noturno/IA, redução temporal própria, estabilização, gravação em background, áudio, controle manual de foco, captura infravermelha. Os perfis chamados **Detalhes** e **Movimento** ainda não possuem tratamento de imagem diferente; para o diagnóstico use **Automático** ou **Manual**.

## Registros permanentes de testes

- [**RELATÓRIO CONSOLIDADO: vídeo, fotos, conclusões e limitações**](RESULTADOS/RELATORIO_CONSOLIDADO.md)
- [**Status dos testes: concluídos e pendentes**](RESULTADOS/STATUS_TESTES.md)
- [Medições por vídeo, CSV](RESULTADOS/videos_medicoes.csv)
- [Medições por fotografia, CSV](RESULTADOS/fotos_medicoes.csv)
- [Amostras da câmera Camera2 com tempo relativo, CSV](RESULTADOS/amostras_camera_sanitizadas.csv)
- [Inventário técnico Camera2/codec sanitizado, JSON](RESULTADOS/inventario_camera_sanitizado.json)

O GitHub é **público**: os MP4/JPEG originais não são enviados e não devem ser publicados aqui. Os registros preservam medições e observações técnicas, mas não substituem o material original para reprocessamento futuro.

## Rodadas de resultados já registradas

- [**Rodada 24/11: três gravações automáticas a 30 FPS e um JPEG**](RESULTADOS/RODADA_2026-11-24/RELATORIO.md)
- [**Medições relativas de 456 amostras Camera2**](RESULTADOS/RODADA_2026-11-24/amostras_sensor_relativas.csv)
- [**Histórico consolidado de todas as rodadas**](RESULTADOS/RELATORIO_CONSOLIDADO.md)
- [**Tabela de acompanhamento dos próximos testes**](RESULTADOS/STATUS_TESTES.md)

O GitHub é **público**. Somente medições e análise são registradas aqui. Não enviar vídeos, fotos, frames extraídos ou timestamps absolutos.

## Protocolo de testes
O documento **[PROTOCOLO_DE_TESTES.md](PROTOCOLO_DE_TESTES.md)** lista **todos os ensaios, configurações e critérios**: variação de FPS, ISO, exposição, resolução, codec, mudanças bruscas de luminosidade, foco, movimento, durabilidade e comparação com câmera Xiaomi.

Os testes anteriores do próprio usuário mostraram **ISO 6400/exposição 66 ms/15 FPS em vídeo** e suporte Camera2 aos FPS 10,15,20,24,30, mas o Lab 0.3 precisa ser testado fisicamente; um recurso anunciado não garante qualidade.

## Obter e instalar o APK

1. Abra [Actions do Vision](https://github.com/vitorsantana1574/Vision/actions).
2. Selecione **Vision Lab - build APK**, confira que a última execução esteja verde.
3. Abra **Artifacts > VisionLab-debug-APK**, baixe o ZIP e extraia `app-debug.apk`.
4. Instale no Redmi 15C. A tela inicial deve mostrar **VISION / Lab 0.4** e o seletor FPS deve conter **10, 15, 20, 24, 30**. No diagnóstico JSON, confira `appVersion: 0.4.0`.
5. **Caso a instalação como atualização falhe:** o GitHub pode assinar cada build debug com uma chave distinta. Exporte os relatórios antigos, desinstale somente o Lab anterior e instale o novo. Os vídeos e fotos na Galeria devem continuar armazenados.

**Android:** mínimo API 35, compilado com Android SDK 35, Java 17, Gradle 8.9; testado em CI com compilação e Android Lint, **sem teste instrumentado no Redmi 15C**.

## Referências técnicas
- https://developer.android.com/reference/android/hardware/camera2/CameraCharacteristics
- https://developer.android.com/reference/android/hardware/camera2/CaptureRequest
- https://developer.android.com/reference/android/media/MediaRecorder

**Aviso:** nenhum software recupera detalhes reais de regiões onde o sensor não registrou luz suficiente.
