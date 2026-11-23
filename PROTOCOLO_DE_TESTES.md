# Vision Lab: protocolo de diagnóstico noturno COM MOVIMENTAÇÃO

**Regra de projeto (atualizada em 23/11/2026):** o uso principal do Vision é gravar no escuro **com o telefone na mão e em movimento**. Não tratar testes com celular imóvel, tripé ou superfície fixa como critério principal de qualidade. Se uma cena estática for usada para calibrar ruído, será apenas um ensaio auxiliar, jamais condição de aprovação.

O APK é **diagnóstico experimental**, não o gravador noturno definitivo. A versão do Lab instalada deve permitir escolher 10, 15 e 30 FPS; o suporte Camera2 por si só não comprova FPS efetivo.

## Preparação (sem tripé)
1. Autorize a câmera e carregue a bateria suficientemente; faça os testes segurando o telefone normalmente.
2. Use sempre o mesmo local, enquadramento aproximado, intensidade de iluminação e percurso. Evite mudar a luz entre os testes.
3. Selecione câmera traseira, resolução **720p**, codec **H.264**, cena nomeada `movimento_quarto`.
4. Não registre outras pessoas sem consentimento e evite materiais privados desnecessários.
5. Execute cada vídeo por cerca de **15–20 segundos**, seguindo o **mesmo percurso dinâmico**:
   - 0–5 s: caminhar devagar apontando para objetos de referência;
   - 5–10 s: giro lateral moderado e retorno;
   - 10–15 s: acompanhar objeto em movimento ou movimentar uma mão diante da câmera;
   - 15–20 s: giro um pouco mais rápido, sem sacudir o aparelho a ponto de impedir a comparação.
6. Anote quando o foco fica perdido, a imagem escurece, surgem rastros, há travamentos ou surgem mensagens de erro.

## Rodada A: FPS com movimento
- Automático / 720p H.264 / **30 FPS**.
- Automático / 720p H.264 / **15 FPS**.
- Automático / 720p H.264 / **10 FPS**.
- Repita com o MESMO deslocamento e iluminação. 10 FPS é experimento, não a recomendação padrão: pode piorar a fluidez sem aumentar exposição.

## Rodada B: sensibilidade versus borrão
- Manual / **15 FPS** / ISO **3200** / exposição **66 ms** (se aceita).
- Manual / **15 FPS** / ISO **6400** / exposição **66 ms** (se aceita).
- Manual / **30 FPS** / ISO **6400** / exposição **33 ms** (se aceita).
- Se o dispositivo limitar a exposição ou rejeitar o perfil, registre a falha, não tente contornar proteções.
- Não solicitar 66 ms com 30 FPS: o tempo de quadro de 30 FPS é apenas ~33,3 ms.

## Rodada C: desafios noturnos reais
1. Caminhar de zona levemente iluminada para mais escura e voltar, avaliando adaptação da exposição.
2. Seguir um objeto em movimento (ou alguém com consentimento), avaliando nitidez de contornos.
3. Fazer giro suave e depois rápido, avaliando rastro e variação de foco.
4. Aproximar-se e afastar-se de superfícies com textura conhecida, avaliando o foco em pouca luz.
5. Testar **1080p H.264 / 15 e 30 FPS** após confirmar 720p, para verificar custo em estabilidade e nitidez.
6. **HEVC** e gravação prolongada (5–10 min caminhando) apenas após os testes curtos estarem corretos.

## Exportações
- Exportar **JSON e CSV ao término** dos testes; salvar os MP4 com nomes originais contendo a cena, o modo e FPS solicitado.
- Informar qual vídeo corresponde a cada cenário, caso algum nome tenha sido alterado.
- Se houver erro na exportação, anotar a mensagem exata e preservar os vídeos. O JSON reporta capacidades anunciadas, e as amostras registram o sensor; isso não equivale ao FPS do arquivo codificado.

## Critérios de aceitação do FUTURO Vision
- Melhor legibilidade de objetos **durante movimento**, sem criar detalhes inexistentes.
- Comparar: brilho útil, ruído aparente, nitidez de textura, **borrão de movimento, ghosting e estabilidade de foco**, adaptação de exposição e velocidade de recuperação após giros.
- Medir FPS *real do MP4*, quadros perdidos/duplicados, duração, travamento, arquivo recuperável, aquecimento e bateria.
- 15 FPS/66 ms pode favorecer brilho, mas 30 FPS/~33 ms pode preservar melhor movimento; a melhor escolha depende do ensaio real.
- **Não aprovar** o aplicativo só por imagens estáticas bonitas. A gravação utilizável com o usuário caminhando é o critério central.

**Limite físico:** software não consegue revelar fielmente detalhes de locais sem luz incidente; iluminação auxiliar é uma alternativa separada, condicionada ao hardware.
