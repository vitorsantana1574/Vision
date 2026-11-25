# Vision Lab 0.4.0 | Rodada comparativa de gravação noturna COM MOVIMENTAÇÃO

**Registro técnico da segunda rodada de 24/11/2026.** Material analisado: 4 vídeos MP4 (IDs 167264, 167265, 167266, 167268), uma cópia JPEG (167267), diagnóstico JSON (inventário e eventos) e CSV. **Nenhum vídeo, fotografia, frame, áudio ou miniatura foi publicado no GitHub.** O repositório é público e contém só medições e conclusões.

**Projeto:** melhorar **vídeos no escuro com Redmi 15C na mão e movimentando-se**. Não se pretende obter apenas imagens paradas mais claras. O laboratório captura sem filtro noturno próprio; os números abaixo são o ponto de partida, NÃO a qualidade futura prometida pelo Vision.

## Identidade e condições

- Aplicativo **confirmado pelo JSON: `appVersion=0.4.0`**, modelo Xiaomi `25078RA3EL`, Android 16; câmera traseira Camera2 ID `0`, hardwareLevel `3`.
- Quatro gravações `Automático (referência)`, todas **1280 × 720 H.264, ~6 Mbps**, sem áudio. A opção manual ISO=1600/exposição=50 ms aparece nos campos *solicitados*, mas é **IGNORADA no modo Automático**; interpretar somente valores reais medidos pelo sensor.
- `thermalStatus=0` no início e no final de cada gravação; isso não atesta estabilidade térmica para gravações longas.
- Sem erros `error_*`, `session_failed`, `recorder_stop_error` ou `video_discarded` nos 22 eventos: **4 videos salvos e 1 JPEG salvo**. A exportação do JSON foi bem sucedida, e o CSV contém 10 amostras adicionais de prévia captadas após a exportação do JSON.

## Integridade e comportamento dos MP4

| ID | Arquivo examinado | FPS pedido/real | Tempo MP4 | Quadros decodificados | Bitrate | ISO mediana | Exposição mediana | Amostras durante vídeo |
|---|---|---|---|---:|---:|---:|---:|---:|
| **A01** | 167264 | 30 / **29,950** | 26,210 s | 785 | 5,996 Mbps | **8200** | **33,299 ms** | 159 |
| **A02** | 167265 | 24 / **23,953** | 24,715 s | 592 | 5,998 Mbps | **7025** | **41,668 ms** | 120 |
| **A04** | 167266 | 15 / **15,018** | 26,502 s | 398 | 6,013 Mbps | **7025** | **41,668 ms** | 81 |
| **EXTRA30** | 167268 | 30 / **29,950** | 20,968 s | 628 | 5,993 Mbps | **8200** | **33,299 ms** | 128 |

- **239?** Não usar soma aproximada: foram **2.403 quadros efetivamente decodificados (785+592+398+628)**.
- Intervalos PTS verificados por `ffprobe` quadro a quadro: A01 ~33,389 ms; A02 ~41,7 ms (pequena variação de arredondamento); A04 ~66,589 ms; EXTRA30 ~33,389 ms. **Zero intervalos >1,5× a mediana** em cada MP4.
- Após decodificação completa e redução de cada quadro a 128×72 em escala de cinza, **não há pares consecutivos exatamente idênticos**; nenhum par teve diferença média inferior a 0,1 nível. Isso não exclui repetição de cena ou limitações do sensor.
- As contagens `sensorFrames` do aplicativo (794, 601, 406, 636) são maiores que os quadros codificados (785, 592, 398, 628), pois há diferença entre eventos de sensor e limites de início/encerramento do arquivo. **Não chamar essa diferença de perda comprovada de quadros**.

## Leitura do ISO e da exposição

- **30 FPS:** ISO 8200, 33,299 ms na quase totalidade das amostras. Estado `AE=4` em todas elas (`CONTROL_AE_STATE_FLASH_REQUIRED`): o Android sinaliza que a iluminação seria insuficiente para boa foto sem flash. Não é ordem para usar flash continuamente durante vídeo.
- **24 FPS:** ISO 7025, 41,668 ms. Aproximadamente **25% a mais de tempo de exposição** por quadro em comparação com 30 FPS, e ISO nominal ~14% menor, **não significando automaticamente 25% mais detalhe real**.
- **15 FPS:** ISO 7025, 41,668 ms, iguais às medianas de 24 FPS. O sensor não prolongou a exposição para 66,7 ms apesar do tempo por quadro disponível.
- A leitura do `CONTROL_AE_STATE` foi 4 em todas as amostras dos quatro vídeos. Os limites de exposição e ISO anunciados pela Camera2 não comprovam que todos os valores sejam utilizáveis em vídeo estável.
- **Decisão provisória:** entre os modos AUTOMÁTICOS examinados, **24 FPS é um candidato forte**: oferece a mesma exposição/ISO de 15 FPS, com movimento temporalmente mais fluido. Ainda precisa ser comparado com 30 FPS por detalhes e movimento na mesma cena.

## Autofoco: dado novo e importante

Estados Camera2: `AF=2` = focado passivamente; `AF=1` = varredura passiva; `AF=6` = varredura passiva terminou sem encontrar foco. São amostras de metadados, não porcentagens de quadros visivelmente desfocados.

| Vídeo | AF focado (=2) | AF procurando (=1) | AF sem foco (=6) | AE=4 |
|---|---:|---:|---:|---:|
| A01 | 146/159 (91,8%) | 11/159 (6,9%) | 2/159 (1,3%) | 159/159 |
| A02 | 113/120 (94,2%) | 5/120 (4,2%) | 2/120 (1,7%) | 120/120 |
| A04 | 49/81 (60,5%) | 12/81 (14,8%) | **20/81 (24,7%)** | 81/81 |
| EXTRA30 | 65/128 (50,8%) | 39/128 (30,5%) | **24/128 (18,8%)** | 128/128 |

**Ressalva:** os enquadramentos, distâncias e áreas escuras são diferentes. Não concluir que o FPS 15 sozinho causa falha de foco. O extra a 30 FPS também teve AF=6 elevado. Este é um provável fator de perda de nitidez que merece análise específica de **foco com movimentação**, não apenas ISO/FPS.

`focusDistanceDiopters` retornou valores **negativos** em alguns instantes, que não representam distância física de foco válida: tratar como telemetria potencialmente inconsistente do HAL Xiaomi/MediaTek. Não usar esse campo isoladamente para calcular metros ou decisões de foco automático.

## Medição das imagens, sem guardar quadros

Todos os MP4 foram decodificados para inspeção e estatísticas **em memória**, sem salvar quadros. A luminância foi medida em ~2 amostras/s, após redução a 256×144 em escala de cinza. Luminância de 0 a 255, **não lux**. Os percentuais se referem à amostra do percurso de cada vídeo.

| ID | Luminância média dos quadros amostrados | Mediana luminância dos quadros | Proporção média de pixels abaixo de 10 | Variância Laplaciana mediana (256×144) |
|---|---:|---:|---:|---:|
| A01 | 55,29 | 54,95 | 21,6% | 134,43 |
| A02 | 48,91 | 50,38 | 18,4% | 63,00 |
| A04 | 35,53 | 39,36 | 16,9% | 22,15 |
| EXTRA30 | 17,41 | 18,43 | **64,4%** | 251,8 |

**NÃO usar esta tabela para classificar resolução/nitidez de formatos**: luminância e Laplaciano mudam com enquadramento, textura, ruído, distância, iluminação e movimento. A imagem mais escura pode ter Laplaciano alto por ruído; a mediana dos fluxos ópticos entre amostras variou, demonstrando que o movimento entre tomadas não é perfeitamente controlado. `EXTRA30` é mais escuro e deve ser analisado separadamente como caso extremo.

**Observação visual da mídia:** textura de cama/parede identificável em partes iluminadas de A01/A02, perda de detalhes e ruído nas regiões escuras; os deslocamentos dificultam separar borrão de exposição e erro de foco. Não usar ampliação artificial para afirmar recuperação de detalhe não capturado. A04 mostra menos contraste na sequência amostrada, mas não se pode responsabilizar isoladamente o FPS 15.

## Sensores de movimento e adequação do APK

`motionSensors` reporta:
- `gyroscopeAvailable: false` (giroscópio **não anunciado** no Android).
- `accelerometerAvailable: true`.
- `rotationVectorAvailable: true` (sensor virtual de orientação disponível, com precisão ainda não verificada).
- `motionSamples` teve **834 amostras**, todas do acelerômetro: A01 222, A02 209, A04 224, EXTRA30 179. Sem amostras de giroscópio.
- Magnitude mediana do acelerômetro: A01 9,464; A02 9,459; A04 9,458; EXTRA30 9,860 m/s². Desvio padrão: **0,131; 0,099; 0,097; 0,018 m/s²**, respectivamente. Em grande parte é o vetor gravidade: **não indica, sozinho, velocidade angular, estabilização ou intensidade total do movimento**. EXTRA30 aparenta menor mudança de aceleração, mas a câmera pode girar suavemente sem que a magnitude revele isso.
- **Lacuna identificada:** coletar também orientação relativa por `TYPE_ROTATION_VECTOR` (se os sensores permitirem), ou estimar movimento dos quadros em processamento, para estudar giros e blur. A versão 0.4 ainda **não fornece orientação relativa**.

## Fotografia de referência

- O arquivo **167267.jpg** recebido tem **1152×1536, 9.062 bytes**, sem ser o JPEG original: `photo_saved` no Lab informa **238.643 bytes**.
- Na cópia recebida: luminância média **6,31/255**, mediana **6**, desvio padrão **5,21**, percentil 99 **19**, com **74,94%** dos pixels abaixo de 10. A cena está muito escura e os detalhes são poucos.
- Log de foto: `still_metadata file=null ISO=675 exposureNs=33330000` foi emitido **após `photo_saved`**. Isso revela **condição de corrida na associação do nome da foto ao resultado Camera2**. Não atribuir metadados de captura à cópia original sem confirmação por EXIF, e **corrigir o mapeamento do nome para próxima versão**.
- A foto não é o eixo principal do projeto; a qualidade de gravação durante movimento tem prioridade.

## Estabilidade e lacunas

- 4/4 MP4 abriram e decodificaram sem erro; nenhum erro de sessão/exportação registrado. O sistema térmico retornou `0` no início e fim, mas **gravações de 21–27 s não comprovam a estabilidade de 5–10 minutos**.
- JSON tem **1063** `captureSamples`; CSV posterior tem **1073**, porque acrescentou **10 amostras de prévia** após o JSON, sem indício de perda ou corrupção.
- No GitHub, [amostras_fps_0_4_sanitizadas.csv](amostras_fps_0_4_sanitizadas.csv) preserva **488 amostras de sensor obtidas DURANTE as quatro gravações**, com tempo relativo, ISO, exposição, FPS do sensor, AE/AF/AWB e foco. **Não contém mídia nem tempos absolutos.**
- Falta verificar 20 FPS, modos manuais (ISO/exposição com movimento), 1080p, HEVC, comparação com câmera original Xiaomi, foco dinâmico em percurso repetido, tempo de adaptação de luz, temperatura após uso prolongado e processamento noturno próprio em tempo real.
- O último algoritmo do Vision ainda não existe; esta rodada apenas mede **a plataforma de captura**, não valida visão noturna melhorada.

## Decisões após esta rodada

1. **Usar 24 FPS/720p/H.264 Automático como hipótese de perfil inicial** para gravação noturna móvel, comparando depois com 30 FPS; não declarar vencedor definitivo.
2. **Investigar AF em pouca luz** antes de implementar realce agressivo. Fazer teste com objeto texturizado próximo/distante e movimento suave, anotando AF=1/2/6.
3. **Melhorar o diagnóstico de movimento** usando `TYPE_ROTATION_VECTOR` (se confiável) e medidas visuais: o acelerômetro em magnitude sozinho é insuficiente.
4. Corrigir `file=null` no registro de metadados JPEG e registrar tamanho/resolução de saída do JPEG real.
5. Repetir o percurso a **20 FPS** e fazer comparação controlada 24 vs 30 com distância semelhante e mesma iluminação. Depois testar manual 24/30 FPS, 1080p e HEVC.
6. Desenvolver mais adiante **denoise temporal com proteção de regiões em movimento, equalização de sombra limitada e estabilização por análise de quadros**, com testes A/B medidos, sem inventar texturas em ausência de luz.

**Fontes:** diagnóstico JSON/CSV exportado pelo próprio Redmi, medições instrumentais locais de MP4/JPEG, documentação oficial das constantes Camera2: https://developer.android.com/reference/android/hardware/camera2/CameraMetadata .

**Privacidade:** não publicar vídeos, fotos, miniaturas, quadros, nomes de lugares, horários absolutos, fingerprints de sistema ou geolocalização. Os originais permanecem nos anexos da conversa até a exclusão pelo usuário. O GitHub preserva somente a análise e medições.
