## Nova rodada: 24/11/2026

Foram analisados três vídeos adicionais (P07–P09, todos automáticos a ~29,95 FPS, 720p/H.264) e uma cópia JPEG (FOTO-P04). Os três MP4 são íntegros, sem lacunas evidentes de timestamp, mas contêm regiões muito escuras; todas as amostras estáveis de vídeo ficaram próximas de ISO 8200 e exposição de 33,3 ms. O arquivo JPEG recebido é muito comprimido em relação ao original indicado no diagnóstico; suas métricas devem ser interpretadas como **cópia compartilhada**, não como qualidade original do sensor.

Os números foram adicionados em [videos_medicoes.csv](videos_medicoes.csv) e [fotos_medicoes.csv](fotos_medicoes.csv), e as 456 novas amostras foram guardadas somente como **dados de sensor relativos** em [RODADA_2026-11-24/amostras_sensor_relativas.csv](RODADA_2026-11-24/amostras_sensor_relativas.csv).

**[Ler análise detalhada com ressalvas, comparação visual e plano de diagnóstico](RODADA_2026-11-24/RELATORIO.md).**

O APK utilizado nesta rodada não confirma as informações de versão/metadados de sessão esperadas no Lab 0.3. A versão 0.4 do laboratório incluirá versão explícita, estados AE/AF, distância de foco e leitura dos sensores de movimento quando disponíveis; seu funcionamento precisa ser comprovado no telefone.

# Vision: registro permanente de ensaios de câmera (Redmi 15C)

**Atualizado em 23/11/2026.** Projeto pessoal Vision; foco em **vídeo noturno segurando o celular em movimento**. Informações derivadas dos arquivos enviados na conversa e de inspeção instrumental (ffprobe/OpenCV e JSON Camera2).

> **Privacidade:** repositório GitHub **público**. Este histórico não contém imagens, vídeos, áudio, localização, fingerprint completo do Android, tempos absolutos das capturas nem dados biométricos. Conservar somente medições e observações técnicas públicas. Os arquivos originais não estão armazenados no GitHub.

## Fonte de verdade para o desenvolvimento

- [Métricas por vídeo: 7 arquivos](videos_medicoes.csv)
- [Métricas por fotografia: 4 arquivos](fotos_medicoes.csv)
- [877 amostras de câmera, com tempos relativos e sem identificadores pessoais](amostras_camera_sanitizadas.csv)
- [Inventário Camera2 e codecs sem fingerprint nem dados de sessão](inventario_camera_sanitizado.json)
- [Plano dos próximos testes em movimento](../PROTOCOLO_DE_TESTES.md)
- [Estado das rodadas](STATUS_TESTES.md)

### Interpretação correta das medições

**FPS de MP4** calculado por `ffprobe` (número de quadros e duração do stream). **ISO, exposição e FPS do sensor** extraídos das amostras de `captureSamples` entre eventos `video_started` e `video_saved`; normalmente são amostrados a cada ~5 quadros, e podem conter alguns quadros de transição entre condições. **Brilho em cinza** e **variância do Laplaciano** foram medidos em quadros amostrados e reduzidos a 256×144, usando a mediana por vídeo. Estes dois últimos valores **não medem qualidade ótica absoluta, nem autorizam ranking entre cenas ou movimentos diferentes**. A unidade de brilho é nível de cinza de 0 a 255, não lux.

## Vídeos já examinados

| Código | MP4 recebido | Modo | FPS pedido/real | ISO mediana | Exposição mediana | Duração | Quadros |
|---|---|---|---|---:|---:|---:|---:|
| P00 | 165894.mp4 | Auto | 30 / 29,95 | 8.200 | 33,299 ms | 10,184 s | 305 |
| P01 | 165899.mp4 | Auto | 30 / 29,95 | 8.200 | 33,299 ms | 10,851 s | 325 |
| P02 | 165900.mp4 | Auto | 15 / 15,018 | 7.025 | 41,668 ms | 12,652 s | 190 |
| P03 | 165901.mp4 | Auto | 10 / 10,011 | 7.025 | 41,668 ms | 11,887 s | 119 |
| P04 | 165902.mp4 | Manual | 15 / 15,018 | 3.200 | 66,000 ms | 11,986 s | 180 |
| P05 | 165903.mp4 | Manual | 15 / 15,018 | 6.400 | 66,000 ms | 10,588 s | 159 |
| P06 | 165904.mp4 | Manual com movimento | 15 / 15,018 | 6.400 | 66,000 ms | 11,653 s | 175 |

Todos os sete MP4 são **1280×720, H.264/AVC, sem áudio**, aproximadamente 6 Mbps. Em todos os arquivos a codificação foi aproximadamente compatível com o FPS solicitado, sem comprovação de ausência total de quadros duplicados.

**P00:** primeira execução do Lab: quatro eventos de foto salva, um vídeo salvo, erro inicial de combinação de stream JPEG 2560×1920 (HAL -38) e falha `ConcurrentModificationException` na primeira tentativa de exportação. O usuário conseguiu fornecer arquivos de diagnóstico; não declarar esta execução completamente livre de falhas.

**P01:** referência automática 30 FPS. Nas amostras mais escuras o sensor se aproximou de ISO 8200 e 33,3 ms por quadro.

**P02:** automático 15 FPS. Mediana de exposição real de 41,7 ms, **não** 66 ms, apesar do intervalo disponível para o FPS.

**P03:** automático 10 FPS. Mediana de exposição continuou em 41,7 ms (com máximo amostrado 66,659 ms), portanto reduzir FPS por si só não garantiu mais luz que P02 na mediana.

**P04:** manual 15 FPS, ISO mediano 3200 e 66 ms. Há algumas amostras transitórias com ISO maior; priorizar a mediana por gravação.

**P05:** manual 15 FPS, ISO mediano 6400, 66 ms; ruído e textura continuam dependentes da cena.

**P06:** ensaio extra com movimento do celular, manual 15 FPS, ISO 6400, 66 ms. Contornos identificáveis em parte do percurso, mas **borrão perceptível em deslocamentos e giros**, com perda severa de informação nas partes quase pretas. Não basta clarear o vídeo; preservar textura em movimento é prioridade.

**Comparabilidade:** a iluminação e o enquadramento não foram perfeitamente constantes. Não existe ainda vencedor definitivo em qualidade noturna. Evitar declarar que P02 é melhor que P01 usando apenas brilho médio ou nitidez matemática entre cenas diferentes.

## Quatro fotografias recebidas

| Arquivo | Dimensões | Brilho médio aproximado (0–255) | Observação |
|---|---|---:|---|
| 165889.jpg | 1152×1536 | 29,41 | objetos pouco visíveis; ruído |
| 165890.jpg | 1152×1536 | 32,72 | baixo contraste e granulação |
| 165891.jpg | 1152×1536 | 6,39 | quase preto; pouco sinal |
| 165892.jpg | 1152×1536 | 70,31 | mais claro; ruído ainda evidente |

As cópias examinadas não contêm EXIF útil para atribuir ISO/exposição a cada foto. Não inferir parâmetros individuais sem metadados originais. Não há fotos armazenadas neste repositório.

## Aparelho e capacidades confirmadas pelo JSON

- Modelo **Redmi 15C / 25078RA3EL**, HyperOS 3.0.307.0, Android 16.
- Câmera traseira selecionada: Camera2 ID `0`, `hardwareLevel=3` (**LEVEL_3**), matriz anunciada 4080×3072, sensor físico 5,22×3,93 mm, abertura anunciada f/1.8.
- ISO anunciado: **50–16000**; exposição anunciada: **0,1–400 ms**. Isso não implica vídeo a 400 ms com FPS elevado.
- Intervalos AE FPS da traseira: **10–10, 15–15, 15–20, 20–20, 24–24, 5–30, 15–30, 30–30**.
- Sem Low Light Boost AE declarado (`false`), sem extensão Night anunciada; modos de estabilização padrão somente `0` (desligado), OIS somente `0`. **Não pressupor estabilização real pela API pública**.
- Codificadores de hardware MediaTek para AVC/H.264 e HEVC/H.265 anunciam suporte a 720p e 1080p em 15/30 FPS. **Gravação real 1080p e HEVC ainda pendentes**.
- OpenGL ES 3.2 anunciado; possibilidade de processamento na GPU, não comprovação de desempenho em vídeo.
- Estado térmico `0` no instante da exportação: não comprova gravação longa livre de aquecimento.

O inventário vinculado contém a lista completa de recursos publicamente reportáveis pela API Camera2 e os nomes das chaves de controles MediaTek. A existência de uma chave não certifica seu funcionamento.

## Status de defeitos

| ID | Problema | Estado |
|---|---|---|
| BUG-01 | Criação de saída JPEG 2560×1920 junto com prévia falhou no Lab inicial | Mitigação implementada: reduzir JPEG; teste real posterior gerou streams 1920×1440 |
| BUG-02 | `ConcurrentModificationException` ao exportar diagnóstico no primeiro Lab | Correção no código (snapshot protegido); relatório de segunda bateria recebido sem evento de erro |
| BUG-03 | Falta de configuração 20/24 FPS para comparar vídeo em movimento | Implementada no Lab 0.3; execução física ainda pendente |
| GAP-01 | Não há processamento noturno próprio (denoise temporal, clareamento local, IA) | Não implementado; dependerá dos testes |
| GAP-02 | Sem estabilização própria e sem áudio | Intencional no Lab de diagnóstico; planejar para aplicativo final |
| GAP-03 | Sem ensaio longo, térmico, foco dinâmico ou 1080p/HEVC real | Pendente |

## Regra para o futuro

**Cada teste novo deve entrar no GitHub antes de os MP4/fotos originais serem descartados.** Atualizar:
1. Uma linha em `videos_medicoes.csv` ou `fotos_medicoes.csv` com parâmetros pedidos, medidos, duração e resultados.
2. As amostras de sensor pertinentes em `amostras_camera_sanitizadas.csv` (ou arquivo separado por rodada), sempre convertendo timestamp absoluto para deslocamento relativo.
3. `STATUS_TESTES.md`, incluindo sucesso/falha, observações, comparações e lacunas.
4. Este relatório com decisões técnicas, limitações e justificativas para mudar o Vision.

Os resultados preservam diagnóstico, **não substituem o vídeo original** se quisermos reprocessar frames por algoritmos futuros. O próximo conjunto está especificado em `PROTOCOLO_DE_TESTES.md`.

## Política de privacidade e exclusão

Sem mídia privada no GitHub público. Não registrar locais exatos, horários absolutos, geolocalização, pessoas filmadas ou fingerprint completo do Android. Excluir os vídeos da conversa ou do dispositivo **não apaga** estes dados técnicos do GitHub automaticamente. Para pedir que o ChatGPT evite mencionar um assunto ou revisar seu resumo de memória, isso é um controle separado nas configurações de personalização.
