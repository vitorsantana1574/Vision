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

## Rodada examinada em 24/11/2026

Resultados completos: [Relatório da rodada](RODADA_2026-11-24/RELATORIO.md) e [amostras relativas](RODADA_2026-11-24/amostras_sensor_relativas.csv). **Sem MP4/JPEG armazenados neste repositório.**

| ID | Material | Diagnóstico |
|---|---|---|
| P07 | 167251.mp4 | Auto 30 FPS, 17,56 s; giros e trechos quase pretos, analisado |
| P08 | 167252.mp4 | Auto 30 FPS, 19,50 s; movimentação e escurecimento intenso, analisado |
| P09 | 167254.mp4 | Auto 30 FPS, 7,58 s; enquadramento mais constante, analisado |
| FOTO-P04 | 167253.jpg | Cópia comprimida; original 207588 bytes segundo app; quase preta, analisada com ressalva |

## Primeira comparação FPS concluída no Lab 0.4 (24/11/2026)

[**Resultados técnicos detalhados: 30/24/15 FPS, extra 30 FPS, foto, foco e movimento**](RODADA_2026-11-24/RELATORIO_COMPARACAO_FPS_LAB04.md).

[**488 amostras do sensor durante os quatro MP4, sem tempos absolutos nem mídia**](RODADA_2026-11-24/amostras_fps_0_4_sanitizadas.csv).

- A01 167264 MP4 30 FPS: **analisado**, 26,210 s, sensor ISO 8200 / 33,299 ms.
- A02 167265 MP4 24 FPS: **analisado**, 24,715 s, sensor ISO 7025 / 41,668 ms.
- A04 167266 MP4 15 FPS: **analisado**, 26,502 s, sensor ISO 7025 / 41,668 ms.
- EXTRA30 167268 MP4 30 FPS: **analisado como cenário muito escuro**, 20,968 s; não é parte do trio comparativo.
- F01 167267 JPG: **apenas parcial**, cópia recebida muito menor que a capturada segundo app.
- **Importante:** A02 tem a mesma mediana de ISO/exposição de A04 e maior FPS. A diferença em AF e luminosidade não prova causalidade por FPS, pois o percurso não foi perfeitamente igual.
- Android 16: sem giroscópio anunciado, com acelerômetro e vetor de rotação disponíveis. Rotação relativa não é coletada pelo Lab 0.4; lacuna para próxima etapa.

## Testes da próxima fase

| ID | Objetivo | FPS pedido | Perfil | Resolução | Codec | Status |
|---|---|---:|---|---|---|---|
| A01 | FPS automatico | 30 | automatico | 720p | H264 | **analisado no Lab 0.4** (167264, 785 quadros; 29.950 FPS) |
| A02 | FPS automatico | 24 | automatico | 720p | H264 | **analisado no Lab 0.4** (167265, 592 quadros; 23.953 FPS) |
| A03 | FPS automatico | 20 | automatico | 720p | H264 | pendente |
| A04 | FPS automatico | 15 | automatico | 720p | H264 | **analisado no Lab 0.4** (167266, 398 quadros; 15.018 FPS) |
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
| F01 | Foto referencia | na | automatico | JPEG | na | **parcial** (167267 cópia reduzida, não JPEG original; 9 KB versus 239 KB reportados) |
| F02 | Camera nativa | 30 | camera_Xiaomi | 1080p | nativo | pendente |

**Totais:** 29 ensaios planejados da fase do Lab; **3 ensaios A01/A02/A04 analisados no Lab 0.4**, F01 parcialmente registrado (não original) e 25 ainda pendentes. O vídeo 167268 é extra e não corresponde a um novo ID A–F.

## Procedimento para atualizar sem perder dados

- **A cada novo envio**, fazer análise dos MP4/JPEG originais, calcular FPS real e intervalos, cruzar ISO/exposição do JSON/CSV e adicionar uma linha às tabelas de resultados.
- Descrever **nitidez em movimento** com observação qualitativa independente das métricas de brilho, que variam com o enquadramento.
- Salvar amostras de sensor **sem timestamps absolutos**, erro do aplicativo e recomendações técnicas.
- Alterar o status pendente para `analisado` apenas após os resultados relevantes entrarem no repositório e uma leitura de verificação confirmar o commit.
- Se um teste falhar, registrar `falhou`, mensagem exata e versão do APK, sem inventar medição ausente.
- **Não publicar os arquivos de vídeo/foto** no repositório público. Os vídeos podem ser apagados da conversa depois da confirmação explícita de que as medidas e observações necessárias foram persistidas; apagá-los elimina a possibilidade de nova análise dos pixels.
- Fonte do roteiro completo: [PROTOCOLO_DE_TESTES.md](../PROTOCOLO_DE_TESTES.md).

**Limite do registro:** persistimos o que foi medido e observado, não todos os pixels dos vídeos. Reprocessamento futuro exigirá os originais ou novas gravações.
