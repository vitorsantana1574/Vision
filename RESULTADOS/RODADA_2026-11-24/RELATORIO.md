# Vision | Rodada experimental de gravação no escuro: 24/11/2026

**Objetivo:** preservar somente resultados e observações técnicas, sem publicar MP4/JPEG. Arquivos examinados temporariamente no atendimento, sem salvar cópias das mídias. Os dados originais enviados pelo usuário permanecem sujeitos ao gerenciamento da própria conversa.

## Materiais examinados

- Três MP4: **167251**, **167252**, **167254**; uma cópia JPEG: **167253**; um JSON de inventário/eventos e um CSV de amostras Camera2.
- Aplicativo observado: **formato de registro legado**. O JSON se declara `v0.1` (valor que também permaneceu fixo em builds posteriores), mas principalmente os eventos `video_started` **não contêm nome do arquivo, FPS detalhado, parâmetros solicitados nem estado térmico por vídeo**, que o código Lab 0.3 deveria registrar. Portanto a **versão instalada NÃO está comprovada como 0.3** e deve ser atualizada antes dos testes 20/24 FPS.
- Modelo no relatório: Redmi 15C, código 25078RA3EL; Camera2 traseira ID `0`. Nenhum erro de gravação ou exportação nos eventos.
- **Não associar estes três arquivos a A01/A02/A03.** Os três foram `Automático` com FPS **30** solicitado e não foram nomeados pelo roteiro; o grupo A de variação 30/24/20/15/10 ainda precisa ser executado.

## Três vídeos medidos

| Teste interno | Vídeo original | Duração | Quadros codificados | FPS calculado do MP4 | Codec / Resolução | Bitrate de vídeo | ISO mediana sensor | Exposição mediana | Amostras sensor durante vídeo |
|---|---|---:|---:|---:|---|---:|---:|---:|---:|
| P07 | 167251 | 17,563 s | 526 | 29,9501 | H.264 / 1280×720 | 6,004 Mbps | 8200 | 33,299 ms | 106 |
| P08 | 167252 | 19,499 s | 584 | 29,9501 | H.264 / 1280×720 | 5,996 Mbps | 8200 | 33,299 ms | 118 |
| P09 | 167254 | 7,579 s | 227 | 29,9501 | H.264 / 1280×720 | 5,989 Mbps | 8200 | 33,299 ms | 47 |

O MP4 codificado tem espaçamento PTS praticamente constante de **33,389 ms** e não mostra intervalos ausentes de tempo. Esta análise **não prova** que não existam quadros quase duplicados. Os vídeos não contêm trilha de áudio.

As amostras de captura indicam quase sempre ISO 8200 e exposição ~33,299 ms **nas três gravações**. O ISO 675 aparece na amostra inicial de transição de cada vídeo, antes de estabilizar a exposição. A faixa técnica do sensor anuncia ISO até 16000, mas a captura automática analisada operou em ~8200. Não inferir que o sistema poderia atingir ISO16000 com qualidade aceitável.

## Inspeção visual minuciosa (amostra periódica, não reconstrução da mídia)

- **P07:** saída da cama/parede relativamente visível para áreas quase pretas e retorno. Textura da cama e contrastes se tornam difíceis de distinguir nos giros e na escuridão. A média de luminância Y do conjunto de quadros amostrados ficou em **12,76 / 255**, com aproximadamente **75%** dos pixels abaixo de Y10 (métrica descritiva desse percurso, não um índice de qualidade).
- **P08:** mudanças de enquadramento mais amplas, envolvendo parede, cama, objetos e regiões muito escuras. Luminância média amostrada **16,30 / 255**, com **68%** dos pixels abaixo de Y10. Os trechos mais pretos não conservam texturas legíveis; há borrão e perda de contorno ao mover o telefone.
- **P09:** enquadramento quase constante da cama/parede, sem giro comparável aos anteriores. Luminância média amostrada **34,27 / 255**, **31%** dos pixels abaixo de Y10. É mais visível por apresentar outra composição/iluminação; **não concluir que houve ganho de processamento**.
- Mediana da variância Laplaciana em miniaturas 320×180: P07 **4,06**, P08 **2,73**, P09 **40,53**, respectivamente. **Não comparar essas quantidades como nota de nitidez entre vídeos diferentes**: a proporção de áreas pretas, a textura, os movimentos e o enquadramento variam fortemente.
- A imagem fica escura mesmo quando o ISO sobe porque o sensor recebe pouca luz por quadro; aumentar somente brilho/contraste também amplifica ruído e compressão.

## Fotografia 167253 (cópia recebida)

- Resolução recebida: **1152×1536**, JPEG com aproximadamente **6,9 KB**. Média de luminância **2,85 / 255**; desvio padrão **3,38**, mediana **2**, percentil 99 **13**; cerca de **96,77%** dos pixels são inferiores a 10.
- O aplicativo registrou **`photo_saved ... 207588 bytes`**, com fluxo JPEG interno 1920×1440 e resultado `still_metadata`: **ISO 675, 33,33 ms**. O arquivo compartilhado é **menor e redimensionado** em relação ao original, sem EXIF útil. Não podemos atribuir sua perda de detalhe ao JPEG original, nem medir ruído óptico absoluto com esta cópia.
- Imagem recebida quase totalmente preta; não é possível revelar fielmente objetos que o sensor não registrou. O contraste entre ISO 675 no evento de foto e ISO 8200 nos vídeos requer outro teste: pode refletir a adaptação de exposição/sessão ou diferenças de cena, e não autoriza afirmar defeito do sensor.

## Integridade de relatórios e APK

- JSON contém **446** amostras; CSV subsequente contém **456**, diferença de **10** quadros de prévia capturados entre exportações. Não caracteriza corrupção.
- **456 amostras preservadas** em [amostras_sensor_relativas.csv](amostras_sensor_relativas.csv), agrupadas em P07 (106), P08 (118), P09 (47), prévia (185). Removidos timestamps absolutos; mantidos ISO, duração da exposição, FPS de sensor e fase de gravação.
- O inventário de capacidades Camera2 do aparelho foi registrado em rodada anterior; não houve mudança observada no hardware.
- **Lacuna do APK testado:** não registra estado real do foco (AF), distância da lente, estado AE/AWB, sensores inerciais, nem identificação inequívoca da versão. A versão **Vision Lab 0.4** foi planejada/implementada no repositório para diagnosticar isso; validar build e teste físico antes de aprovar.

## Decisões técnicas para Vision

1. Priorizar **imagem utilizável durante movimento**, não apenas vídeo parado mais claro.
2. A **30 FPS / 33,3 ms / ISO8200**, trechos muito escuros permanecem próximos do limite útil. Aumentar resolução ou bitrate **não captura mais fótons**.
3. Precisamos comparar 15, 20, 24 e 30 FPS no **mesmo percurso e luz** antes de determinar o perfil padrão, porque esta rodada trouxe apenas 30 FPS.
4. Medir foco e movimento com a nova instrumentação para separar perda por desfoque, balanço da câmera e baixo sinal. Se o celular não possuir giroscópio, registrar o fato e usar métodos visuais.
5. Preparar **redução de ruído temporal sensível ao movimento**, realce moderado de sombra e exposição adaptativa, com teste A/B sempre usando vídeos reais. Nenhum desses processamentos ainda é entregue pelo Lab.
6. Um iluminador externo pode auxiliar em escuridão quase total, mas **infravermelho exige comprovar sensibilidade do conjunto óptico**; não oferecer promessa sem teste.

## Próximo ensaio mínimo útil

Depois de instalar uma versão com título de tela e `appVersion` verificáveis, faça três vídeos de ~20 s do mesmo percurso no escuro, em `Automático, 720p, H.264`, variando **30/24/15 FPS**. Exporte JSON e CSV no final. Se confirmado o ganho, completaremos a matriz de 20/10 FPS, manual e 1080p/HEVC. Nunca use um local inseguro para caminhar.

**Política de dados:** o GitHub guarda exclusivamente métricas e texto deste ensaio, sem frames, áudio, fotografias, MP4 ou imagens derivadas. Os vídeos originais ainda são necessários apenas se o usuário desejar reanálise/reprocessamento futuro e poderão ser eliminados pelo próprio usuário depois de confirmar o registro.
