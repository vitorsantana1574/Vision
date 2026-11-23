# Vision | Status de ensaios

**Registro de avanço do laboratório.** Os vídeos antigos P00–P06 estão examinados em [RELATORIO_CONSOLIDADO.md](RELATORIO_CONSOLIDADO.md). As quatro imagens iniciais estão em [fotos_medicoes.csv](fotos_medicoes.csv). Os testes abaixo ainda não foram entregues nem examinados; NÃO marcar como aprovados sem receber e verificar os arquivos correspondentes.

## Rodadas concluídas anteriormente

| ID | Mídia | Verificação |
|---|---|---|
| P00 | 165894.mp4 | Vídeo piloto 30 FPS, arquivo válido, erro antigo na exportação |
| P01 | 165899.mp4 | Automático 30 FPS, analisado |
| P02 | 165900.mp4 | Automático 15 FPS, analisado |
| P03 | 165901.mp4 | Automático 10 FPS, analisado |
| P04 | 165902.mp4 | Manual 15 FPS, mediana ISO 3200, analisado |
| P05 | 165903.mp4 | Manual 15 FPS, mediana ISO 6400, analisado |
| P06 | 165904.mp4 | Manual 15 FPS, ISO 6400, movimentação, analisado |
| FOTO-P00–P03 | 165889.jpg–165892.jpg | Cópias analisadas, EXIF indisponível |

## Testes da próxima fase

| ID | Objetivo | FPS pedido | Perfil | Resolução | Codec | Status |
|---|---|---:|---|---|---|---|
| A01 | FPS automatico | 30 | automatico | 720p | H264 | pendente |
| A02 | FPS automatico | 24 | automatico | 720p | H264 | pendente |
| A03 | FPS automatico | 20 | automatico | 720p | H264 | pendente |
| A04 | FPS automatico | 15 | automatico | 720p | H264 | pendente |
| A05 | FPS automatico | 10 | automatico | 720p | H264 | pendente |
| B01 | ISO exposicao | 15 | manual_ISO3200_E66ms | 720p | H264 | pendente |
| B02 | ISO exposicao | 15 | manual_ISO6400_E66ms | 720p | H264 | pendente |
| B03 | ISO exposicao | 15 | manual_ISO8000_E66ms | 720p | H264 | pendente |
| B04 | ISO exposicao | 20 | manual_ISO3200_E49ms | 720p | H264 | pendente |
| B05 | ISO exposicao | 20 | manual_ISO6400_E49ms | 720p | H264 | pendente |
| B06 | ISO exposicao | 24 | manual_ISO3200_E41ms | 720p | H264 | pendente |
| B07 | ISO exposicao | 24 | manual_ISO6400_E41ms | 720p | H264 | pendente |
| B08 | ISO exposicao | 30 | manual_ISO3200_E33ms | 720p | H264 | pendente |
| B09 | ISO exposicao | 30 | manual_ISO6400_E33ms | 720p | H264 | pendente |
| B10 | ISO exposicao | 30 | manual_ISO8000_E33ms | 720p | H264 | pendente |
| C01 | Resolucao codec | 15 | automatico | 1080p | H264 | pendente |
| C02 | Resolucao codec | 24 | automatico | 1080p | H264 | pendente |
| C03 | Resolucao codec | 30 | automatico | 1080p | H264 | pendente |
| C04 | Resolucao codec | 24 | automatico | 720p | HEVC | pendente |
| C05 | Resolucao codec | 24 | automatico | 1080p | HEVC | pendente |
| D01 | transicao_luz | 24 | automatico | 720p | H264 | pendente |
| D02 | transicao_luz | 30 | automatico | 720p | H264 | pendente |
| D03 | foco_aproxima | 24 | automatico | 720p | H264 | pendente |
| D04 | foco_aproxima | 30 | automatico | 720p | H264 | pendente |
| E01 | Estabilidade 5min | 24 | automatico | 720p | H264 | pendente |
| E02 | Estabilidade 5min | 24 | automatico | 1080p | H264 | pendente |
| E03 | Home durante gravacao | 24 | automatico | 720p | H264 | pendente |
| F01 | Foto referencia | na | automatico | JPEG | na | pendente |
| F02 | Camera nativa | 30 | camera_Xiaomi | 1080p | nativo | pendente |

**Totais:** 29 ensaios planejados para a fase Lab 0.3; 0 ensaios desta fase entregues até a atualização de 23/11/2026. Não misturar os P00–P06 antigos com novos IDs A–F.

## Procedimento para atualizar sem perder dados

- **A cada novo envio**, fazer análise dos MP4/JPEG originais, calcular FPS real e intervalos, cruzar ISO/exposição do JSON/CSV e adicionar uma linha às tabelas de resultados.
- Descrever **nitidez em movimento** com observação qualitativa independente das métricas de brilho, que variam com o enquadramento.
- Salvar amostras de sensor **sem timestamps absolutos**, erro do aplicativo e recomendações técnicas.
- Alterar o status pendente para `analisado` apenas após os resultados relevantes entrarem no repositório e uma leitura de verificação confirmar o commit.
- Se um teste falhar, registrar `falhou`, mensagem exata e versão do APK, sem inventar medição ausente.
- **Não publicar os arquivos de vídeo/foto** no repositório público. Os vídeos podem ser apagados da conversa depois da confirmação explícita de que as medidas e observações necessárias foram persistidas; apagá-los elimina a possibilidade de nova análise dos pixels.
- Fonte do roteiro completo: [PROTOCOLO_DE_TESTES.md](../PROTOCOLO_DE_TESTES.md).

**Limite do registro:** persistimos o que foi medido e observado, não todos os pixels dos vídeos. Reprocessamento futuro exigirá os originais ou novas gravações.
