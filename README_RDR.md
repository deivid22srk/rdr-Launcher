# Red Dead Redemption 1 - Winlator Custom Build

## 📝 Sobre
Esta é uma versão modificada do Winlator especificamente otimizada para rodar Red Dead Redemption 1 no Android. O app inicia automaticamente o jogo sem necessidade de configuração manual.

## ✨ Características

### Auto-Inicialização
- Ao abrir o app, o jogo inicia automaticamente
- Sem necessidade de configurar containers ou shortcuts
- Tudo é configurado automaticamente

### Configurações Pré-Otimizadas

#### Graphics Driver
- **Turnip 25.1.0** (driver Vulkan para Adreno GPUs)
- Adrenotools Turnip ativado
- Frame Sync: Normal

#### DXVK
- **Versão: 2.3.1-arm64ec-gplasync**
- Async: Ativado
- Async Cache: Ativado
- Framerate: Ilimitado
- Max Device Memory: Automático

#### FEXCore (Emulador ARM64)
- **Versão: 2508**
- TSOMode: Fastest (máxima performance)
- X87Mode: Fast (precisão reduzida para melhor FPS)
- MultiBlock: Enabled (otimização de blocos)

#### Outras Configurações
- Startup Selection: Aggressive (para serviços)
- Audio Driver: ALSA Reflector
- Box64 Preset: Performance
- Wine Components: d3dx9, vkd3d, vcrun2019, fontes

## 📦 Estrutura OBB

### Localização do OBB
O jogo deve estar em um arquivo OBB localizado em:
```
/storage/emulated/0/Android/obb/com.rdr.winlator/
```

### Estrutura Interna do OBB
Dentro do OBB (que pode ser um arquivo .zip renomeado para .obb), a estrutura deve ser:
```
Red Dead Redemption/
├── RDR.exe
├── [outros arquivos do jogo]
└── ...
```

### Como Criar o OBB

1. **Prepare os arquivos do jogo:**
   ```bash
   mkdir "Red Dead Redemption"
   # Copie todos os arquivos do jogo para esta pasta
   ```

2. **Crie o arquivo OBB:**
   ```bash
   zip -r main.1.com.rdr.winlator.obb "Red Dead Redemption"
   ```

3. **Instale o OBB no dispositivo:**
   ```bash
   adb push main.1.com.rdr.winlator.obb /storage/emulated/0/Android/obb/com.rdr.winlator/
   ```

## 🎮 Controles

O app inclui um perfil de controles pré-configurado para Red Dead Redemption:

### Controles Touchscreen
- **D-Pad (Esquerda):** WASD - Movimentação
- **Botão A (Direita inferior):** Espaço - Pular
- **Botão B (Direita):** Click Direito - Mirar/Bloquear
- **Botão X (Direita superior):** Click Esquerdo - Atirar/Atacar
- **Botão Y:** R - Recarregar
- **LB:** Q - Cobertura
- **RB:** E - Dead Eye
- **Botão Interagir:** F
- **Botão Cavalo:** H - Chamar cavalo
- **Mapa:** M
- **Inventário:** I
- **Pausa:** ESC
- **Joystick Direito:** Mouse Look - Câmera
- **Sprint:** Shift
- **Agachar:** Ctrl

### Gamepad
O app suporta controles externos via USB/Bluetooth com mapeamento automático.

## 🔧 Compilação

### Requisitos
- Android Studio (última versão)
- NDK 27.0.12077973
- JDK 17
- Gradle 8.8.0

### Passos para Compilar

1. **Abra o projeto no Android Studio:**
   ```bash
   cd winlator-cmod_bionic
   ```

2. **Instale as dependências:**
   - O Gradle irá baixar automaticamente

3. **Compile o APK:**
   ```bash
   ./gradlew assembleDebug
   # ou
   ./gradlew assembleRelease
   ```

4. **APK gerado em:**
   ```
   app/build/outputs/apk/debug/app-debug.apk
   # ou
   app/build/outputs/apk/release/app-release.apk
   ```

## 📱 Instalação

1. **Instale o APK:**
   ```bash
   adb install app-debug.apk
   ```

2. **Copie o OBB para o dispositivo:**
   ```bash
   adb shell mkdir -p /storage/emulated/0/Android/obb/com.rdr.winlator
   adb push main.1.com.rdr.winlator.obb /storage/emulated/0/Android/obb/com.rdr.winlator/
   ```

3. **Execute o app:**
   - O jogo iniciará automaticamente após a primeira configuração

## ⚙️ Variáveis de Ambiente Aplicadas

```bash
ZINK_DESCRIPTORS=lazy
ZINK_DEBUG=compact
MESA_SHADER_CACHE_DISABLE=false
MESA_SHADER_CACHE_MAX_SIZE=2G
mesa_glthread=true
GALLIUM_DRIVER=zink
MESA_NO_ERROR=1
vblank_mode=0
DXVK_ASYNC=1
DXVK_GPLASYNCCACHE=1
__GL_THREADED_OPTIMIZATIONS=1
```

## 🐛 Solução de Problemas

### Jogo não inicia
1. Verifique se o OBB está no local correto
2. Verifique se RDR.exe existe dentro do OBB
3. Verifique as permissões de armazenamento

### Performance ruim
1. Ajuste a resolução (padrão: 1280x720)
2. Verifique se seu dispositivo tem GPU Adreno
3. Certifique-se que não há outros apps consumindo recursos

### Controles não funcionam
1. Calibre o touchscreen nas configurações do Android
2. Para gamepad, reconecte o controle
3. Verifique se o perfil de controles está carregado

## 📄 Créditos

### Projeto Original
- **Winlator** por Brunodev85
- **Winlator Cmod** por coffincolors, Pipetto-crypto

### Componentes
- Wine (winehq.org)
- Box86/Box64 (ptitSeb)
- DXVK (doitsujin)
- Mesa Turnip/Zink
- FEXCore

### Modificações RDR
- Auto-inicialização customizada
- Configurações otimizadas para RDR1
- Sistema OBB integrado
- Perfil de controles específico

## 📜 Licença
Este projeto mantém a licença original do Winlator. Consulte o arquivo LICENSE para mais detalhes.

## ⚠️ Aviso Legal
Este projeto é apenas para uso educacional. Você deve possuir uma cópia legítima de Red Dead Redemption para usar este emulador.
