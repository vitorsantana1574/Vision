# Vision Lab 0.4 | Protocolo completo de diagnóstico dinâmico do Redmi 15C

**Objetivo exclusivo:** encontrar o perfil de **GRAVAÇÃO NOTURNA COM O CELULAR NA MÃO E EM MOVIMENTO** que mantém melhor legibilidade real, com ruído, borrão e travamentos aceitáveis. Fotos são secundárias. Este roteiro é específico do Redmi 15C 25078RA3EL, Android 16, HyperOS 3, conforme os relatórios obtidos no próprio aparelho.

**Status:** protocolo de ensaio. O Vision Lab 0.4 apenas captura e registra dados; **não contém IA, realce de sombra, estabilização digital nem redução temporal própria**. O aparelho não anuncia Low Light Boost nem extensões Night na Camera2. Não usar esses recursos como se estivessem implementados.

## Prioridade imediata após a rodada P07–P09

Antes de iniciar os 29 testes completos, **instale e verifique a versão 0.4.0**: a tela deve exibir `VISION / Lab 0.4`, o seletor deve incluir **10/15/20/24/30 FPS**, e o novo JSON deve conter `appVersion: 0.4.0` e `motionSensors`. Se o APK foi assinado por outro runner de GitHub, poderá ser necessário desinstalar a versão de debug anterior, **após exportar relatórios**; mantenha os vídeos e fotos da Galeria intactos.

O primeiro lote de comparação controlada será só **A01 (30 FPS), A02 (24 FPS) e A04 (15 FPS)**, todos em 720p/H.264, 20 segundos, mesmo cenário, percurso e luminosidade, **com o celular na mão em movimento**. Envie os três MP4 originais, JSON e CSV. Só após a análise objetiva desse lote avançaremos para os demais ensaios, evitando repetir gravações desnecessárias.

**Novo diagnóstico:** 0.4 captura, se os sensores existirem, giroscópio/acelerômetro no campo `motionSamples` do JSON; também registra estados AE/AF/AWB e distância de foco em `captureSamples`, com vínculo `clip`. Não transfere fotos ou vídeos para a internet. A presença e a qualidade destes dados só serão confirmadas após teste real.

Os vídeos P07–P09 antigos foram analisados e preservados em [RESULTADOS/RODADA_2026-11-24/RELATORIO.md](RESULTADOS/RODADA_2026-11-24/RELATORIO.md), sem arquivos de mídia publicados.

## 0. Verifique a versão e prepare o telefone

- Instale o **Lab 0.4.0**, que mostra `VISION / Lab 0.4` no topo, e confirme os FPS **10 / 15 / 20 / 24 / 30**. Se não aparecem 20 e 24 FPS, você está usando um APK antigo.
- Se o Android mostrar *Aplicativo não instalado* ao atualizar, as chaves de depuração entre compilações do GitHub podem ter mudado. Desinstale o Lab antigo e instale o novo. **Preserve os MP4/JPEG e exporte JSON/CSV antes de desinstalar.** Não remova a pasta da Galeria.
- A câmera deve estar **traseira**, com zoom físico **1x**, sem flash. Segure o Redmi em **orientação vertical**: o aplicativo está programado para retrato.
- Bateria inicial preferencialmente **>= 70%**, sem carregador durante a avaliação térmica, e anote porcentagem inicial/final. Pare se houver aviso de aquecimento, desconforto térmico, perda de bateria excessiva ou risco de queda.
- Ambiente **escuro, mas seguro para caminhar**. Não caminhe às cegas. Evite filmar outras pessoas sem permissão.
- Nos testes comparáveis, mantenha a mesma iluminação, local, roupa/cena e aproximadamente o mesmo percurso. Não altere a luz entre gravações.
- **Sempre nomeie a cena pelo ID da tabela**, p.ex. `A01`. Selecione perfil, resolução, codec e FPS; insira ISO/exposição quando o perfil for Manual. Toque **Aplicar perfil / exposição**, aguarde a prévia e inicie a gravação. Em Automático, ISO/exposição digitados são ignorados.
- Mantenha os nomes originais dos MP4; seus sufixos indicam modo `M0/M4` e FPS solicitado `F10/F15/F20/F24/F30`. O FPS real precisa ser confirmado no arquivo.

## Percurso padrão de 20 segundos (para cada teste curto)

1. 0–5 s: caminhe devagar com o telefone na mão e aponte para um objeto texturizado.
2. 5–10 s: faça uma panorâmica lateral moderada e retorne.
3. 10–15 s: movimente sua mão ou um objeto na frente da câmera (não precisa incluir outras pessoas).
4. 15–20 s: caminhe virando o celular mais rápido e retorne ao mesmo enquadramento.
5. Conte mentalmente os tempos e repita o mais parecido possível. **Não use tripé nem apoio fixo como requisito de teste.**

## A. FPS automático, 720p/H.264 (5 vídeos obrigatórios)

| ID | Perfil | FPS | ISO | Exposição | Duração |
|---|---|---:|---|---|---|
| A01 | Automático | 30 | AUTO | AUTO | 20 s |
| A02 | Automático | 24 | AUTO | AUTO | 20 s |
| A03 | Automático | 20 | AUTO | AUTO | 20 s |
| A04 | Automático | 15 | AUTO | AUTO | 20 s |
| A05 | Automático | 10 | AUTO | AUTO | 20 s |

**Motivo:** dados anteriores mostraram que a captura automática a 10 FPS não aumentou a exposição em relação a 15 FPS. Repetiremos com o mesmo movimento para verificar se 20 ou 24 FPS oferecem melhor equilíbrio.

## B. ISO/exposição manuais, 720p/H.264 (10 vídeos obrigatórios)

Selecione **Manual ISO/exposição**. Exposição em **milissegundos**, não segundos:

| ID | FPS | ISO | Exposição (ms) | Duração |
|---|---:|---:|---:|---|
| B01 | 15 | 3200 | 66 | 20 s |
| B02 | 15 | 6400 | 66 | 20 s |
| B03 | 15 | 8000 | 66 | 20 s |
| B04 | 20 | 3200 | 49 | 20 s |
| B05 | 20 | 6400 | 49 | 20 s |
| B06 | 24 | 3200 | 41 | 20 s |
| B07 | 24 | 6400 | 41 | 20 s |
| B08 | 30 | 3200 | 33 | 20 s |
| B09 | 30 | 6400 | 33 | 20 s |
| B10 | 30 | 8000 | 33 | 20 s |

**Motivo:** isolar ganho, tempo de exposição e rastro de movimento. ISO alto pode aumentar ruído. A Camera2 anunciou suporte a todos esses valores, mas a captura efetiva deve ser conferida no JSON. O aplicativo limita exposições que excedem o tempo por quadro.

## C. Resolução e codec (5 vídeos curtos condicionais)

Execute **após A/B funcionarem**, usando o percurso e luz iguais. Perfil Automático, exceto onde indicado:

| ID | Resolução | Codec | FPS | Duração |
|---|---|---|---:|---|
| C01 | 1080p | H.264 | 15 | 20 s |
| C02 | 1080p | H.264 | 24 | 20 s |
| C03 | 1080p | H.264 | 30 | 20 s |
| C04 | 720p | H.265/HEVC | 24 | 20 s |
| C05 | 1080p | H.265/HEVC | 24 | 20 s |

Compare com A02/A04/A01 e considere possíveis erros de codec ou travamento. **1080p não significa necessariamente melhora de sensibilidade noturna.** H.265 também não acrescenta luz ao sensor: ajuda na compressão.

## D. Desafios dinâmicos dirigidos (4 vídeos)

Perfil Automático, 720p/H.264:

| ID | FPS | Percurso | Duração |
|---|---:|---|---|
| D01 | 24 | Caminhe de parte um pouco iluminada para parte escura e retorne | 25 s |
| D02 | 30 | Repita a transição de D01 | 25 s |
| D03 | 24 | Aproxime e afaste de texturas conhecidas de 0,5 m a 2 m com movimento | 25 s |
| D04 | 30 | Repita D03, mantendo a velocidade aproximada | 25 s |

**Observar:** adaptação da exposição e do foco, perda de detalhe e recuperação após movimento.

## E. Estabilidade operacional e autonomia (3 ensaios)

| ID | Configuração | Ação | Duração |
|---|---|---|---|
| E01 | 720p H.264 Automático 24 FPS | Caminhar e variar enquadramento de maneira natural | 5 min |
| E02 | 1080p H.264 Automático 24 FPS | Caminhar e fazer panorâmicas | 5 min |
| E03 | 720p H.264 Automático 24 FPS | Comece gravação, toque Home, volte; confirme se o app encerra e preserva o vídeo | ~30 s |

O Lab **não grava em segundo plano**: ao sair da tela ele encerra a gravação. Isso é comportamento esperado do protótipo, mas precisamos confirmar que o arquivo não corrompe. **NÃO TESTE gravação de tela apagada como recurso prometido.**

Anote bateria antes/depois de E01 e E02 e presença de aviso térmico. Um vídeo de até **15 minutos** poderá ser solicitado depois, apenas se os primeiros 5 minutos forem confiáveis.

## F. Fotografia secundária e comparação nativa

- **F01:** tire uma JPEG com o Lab em Automático enquanto segura a câmera normalmente (luz baixa). Confirme que aparece em `Pictures/VisionLab`. Este teste verifica captura/salvamento, não define a qualidade principal.
- **F02:** grave um vídeo pelo aplicativo nativo Xiaomi, idealmente **1080p / 30 FPS com taxa automática ativa**, repetindo o percurso da A01 ou C03. Preserve o arquivo original para comparação.
- **F03 (opcional):** mais um vídeo com o app nativo em 720p, se a opção existir. Registrar quaisquer configurações utilizadas.

## Exportar após cada bloco

1. Pare a gravação. Exporte **JSON e CSV** ao terminar A/B, depois C/D, depois E/F. Os relatórios misturam todas as amostras desde que o app foi aberto; **exportar por bloco e reiniciar entre blocos mantém cada arquivo legível**.
2. Envie MP4 originais (não comprima via mensageiro), JSON e CSV. Se compartilhar em lote, compacte ZIP por bloco.
3. Conte qualquer falha de prévia, foco, travamento, tela preta, arquivo ausente, aquecimento ou erro de exportação e associe ao ID.
4. Não apague nem renomeie os arquivos antes de guardá-los, nem instale uma compilação nova no meio de uma sequência de testes.

## Limitações do Lab 0.3 conhecidas no código

- **Modos "Detalhes" e "Movimento" ainda não têm filtros de vídeo dedicados.** O processamento do Vision definitivo NÃO está implementado. Use **Automático** ou **Manual** na matriz.
- **Sem áudio** (não pede microfone), sem estabilização própria, sem controle manual de foco na interface, sem indicador de lux e sem gravação em background. Não interpretar isso como falha nos recursos não implementados.
- A câmera traseira anuncia **somente estabilização OFF (0)** nas APIs padrão; estabilização física/digital por outro caminho não foi validada.
- O APK pede o FPS e captura metadados, mas o FPS efetivamente codificado será checado nos MP4, não inferido pelo seletor.
- A chave de assinatura `debug` do GitHub Actions pode mudar entre builds. Se a atualização sobre a versão anterior falhar, exporte dados e **desinstale somente o aplicativo**, não os arquivos da Galeria.
- Exposição real não segue necessariamente o valor solicitado, e a comparação requer condições semelhantes.
- **Não é visão noturna infravermelha nem consegue enxergar fielmente locais sem luz.**

## Critérios para avançar ao gravador noturno Vision

Validar a melhor relação entre claridade útil e **nitidez em movimento**, erros de compressão, rastro/ghosting, foco, luminosidade ao caminhar, FPS real, perda/duplicação de quadros, integridade de arquivo, tempo de recuperação de exposição e comportamento térmico.

**A qualidade do vídeo com o celular na mão vale mais do que fotos estáticas bem iluminadas.**
