# Vision Lab 0.1: protocolo de testes no Redmi 15C

Este APK é de **diagnóstico**, não de melhoria noturna. Teste com autorização, sem expor imagens pessoais desnecessárias.

## Sequência
1. Instale o APK de teste e autorize a câmera. Conecte o carregador se a bateria estiver baixa.
2. Pressione **Exportar diagnóstico + amostras JSON** antes de testar. Salve em Downloads.
3. Escolha a cena `quarto`. Apoie o telefone em superfície estável; não altere a iluminação durante a comparação.
4. No perfil **Automático**, tire uma foto JPEG e grave um MP4 de 20 segundos em 720p/H.264.
5. Repita com **Detalhes**, **Movimento** e **Low Light Boost (se disponível)**. Não estranhe se algum modo não melhorar ou falhar.
6. Repita uma rodada em 1080p/H.264, somente se o aparelho suportar e após confirmar a versão 720p.
7. Para a cena `manga`, faça fotos e vídeos de referência sob a mesma condição de luz e com o telefone parado. O software não revela objetos que não refletiram luz ao sensor.
8. Grave uma cena com objeto se movendo lentamente para comparar borrão e continuidade.
9. Para ISO manual, teste apenas valores aceitos pelo relatório Camera2, começando com exposição curta. Se a câmera não expuser MANUAL_SENSOR, não use o modo.
10. Exporte novamente o JSON e o CSV. Os arquivos de imagem e vídeo aparecem em Pictures/VisionLab e Movies/VisionLab.

## Arquivos para análise
- Primeiro e último JSON.
- CSV de amostras.
- 3 a 6 fotos de referência e alguns MP4 curtos com o nome da cena preservado.
- Se algum modo falhar, descreva a mensagem e o momento exato.

## Critérios de teste
- Confirmação do nível Camera2 (LEGACY/LIMITED/FULL/LEVEL_3).
- Faixas ISO, exposição, FPS anunciados vs. FPS efetivamente observado.
- Resoluções aceitas e codecs H.264/HEVC suportados.
- Fotografia legível e vídeo gravado/aberto na Galeria.
- Comparação visual de detalhes e borrão sob a mesma iluminação.
- 5 a 10 minutos de gravação 720p depois que os testes curtos passarem.
- Qualquer aquecimento, interrupção, tela preta ou arquivo corrompido é falha relevante.

**Importante:** o FPS estimado por `SENSOR_TIMESTAMP` não equivale necessariamente ao FPS efetivamente codificado no MP4. Nenhuma melhoria por GPU/IA foi implementada ainda.
