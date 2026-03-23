// Variáveis Globais
let credencialAdmin = null;
let brindesLista = [];
let leitorAtivo = false;
let html5QrcodeScanner = null;

// Elementos do DOM
const searchInput = document.getElementById('searchInput');
const clientsTableBody = document.getElementById('clientsTableBody');
const totalCount = document.getElementById('totalCount');
const btnLerQrCode = document.getElementById('btnLerQrCode');
const scannerContainer = document.getElementById('scanner-container');
const btnFecharScanner = document.getElementById('btnFecharScanner');
const divResultadoScanner = document.getElementById('resultado-scanner');

// 1. MÓDULO DE AUTENTICAÇÃO
function pedirCredenciais() {
    if (credencialAdmin) return true; // Já está logado

    let usuario = prompt("🔒 Área Restrita - Dashboard\nDigite o usuário Admin:");
    if (!usuario) return false;

    let senha = prompt("🔑 Digite a senha:");
    if (!senha) return false;

    credencialAdmin = btoa(usuario + ":" + senha);
    return true;
}

// 2. MÓDULO DA API (BUSCAR DADOS)
async function fetchBrindes() {
    if (!pedirCredenciais()) {
        clientsTableBody.innerHTML = `<tr><td colspan="4" class="no-results">Acesso negado. Recarregue a página para logar.</td></tr>`;
        return;
    }

    try {
        // Busca a página 0 com até 100 itens (Ajuste a URL conforme o seu Controller)
        const response = await fetch('http://localhost:8080/cadastros?page=0&size=100', {
            method: 'GET',
            headers: { 'Authorization': 'Basic ' + credencialAdmin }
        });

        if (response.status === 401) {
            alert("❌ Usuário ou senha incorretos!");
            credencialAdmin = null;
            return;
        }

        const data = await response.json();

        // Como a API é paginada, os dados ficam dentro do array "content"
        brindesLista = data.content || data;
        renderTable(brindesLista);

    } catch (error) {
        console.error("Erro na API:", error);
        clientsTableBody.innerHTML = `<tr><td colspan="4" class="no-results" style="color: red;">Erro ao conectar com o servidor Java.</td></tr>`;
    }
}

// 3. RENDERIZAÇÃO DA TABELA
function createStatusBadge(status) {
    // Usa a classe CSS exata dependendo da palavra (PENDENTE ou RESGATADO)
    return `<span class="badge badge-${status}">${status}</span>`;
}

function renderTable(lista) {
    if (lista.length === 0) {
        clientsTableBody.innerHTML = `<tr><td colspan="4" class="no-results">Nenhum brinde encontrado</td></tr>`;
        totalCount.textContent = '0';
        return;
    }

    const rows = lista.map(brinde => `
        <tr>
            <td><strong>#${brinde.id}</strong></td>
            <td>${brinde.nome || '-'}</td>
            <td>${brinde.email}</td>
            <td>${createStatusBadge(brinde.status)}</td>
            <td class="text-right">
                <div class="actions">
                    ${brinde.status === 'PENDENTE' ? `
                        <button class="btn btn-primary" style="padding: 0.25rem 0.5rem; font-size: 0.75rem;" onclick="resgateManual(${brinde.id})" title="Aprovar Manualmente">
                            Aprovar Bypass
                        </button>
                    ` : `
                        <span style="font-size: 0.75rem; color: #9ca3af;">Já Resgatado</span>
                    `}
                    <button class="btn btn-ghost" style="color: #ef4444; padding: 0.25rem;" onclick="deletarBrinde(${brinde.id})" title="Excluir Registro">
                                                <svg class="icon icon-delete" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="width: 1.25rem; height: 1.25rem;">
                                                  <polyline points="3 6 5 6 21 6"/>
                                                  <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                                                </svg>
                                            </button>
                </div>
            </td>
        </tr>
    `).join('');

    clientsTableBody.innerHTML = rows;
    totalCount.textContent = lista.length;
}

// 4. MÓDULO DE AÇÕES (BYPASS MANUAL)
async function resgateManual(id) {
    if (!confirm(`Deseja aprovar manualmente o brinde ID #${id}? Essa ação não pode ser desfeita.`)) return;

    try {
        const response = await fetch(`http://localhost:8080/qrcode/resgatar/${id}`, {
            method: 'PUT',
            headers: { 'Authorization': 'Basic ' + credencialAdmin }
        });

        if (response.ok) {
            alert("✅ Brinde resgatado manualmente com sucesso!");
            fetchBrindes(); // Recarrega a tabela atualizada
        } else {
            const errorData = await response.json();
            alert("❌ Erro: " + (errorData.mensagemErro || "Falha na operação."));
        }
    } catch (error) {
        alert("❌ Erro de conexão com o servidor.");
    }
}

async function deletarBrinde(id) {
    // Trava de segurança para evitar cliques acidentais
    if (!confirm(`⚠️ Atenção: Deseja realmente excluir o brinde #${id}? \nEssa ação não pode ser desfeita e o QR Code do cliente deixará de funcionar.`)) {
        return;
    }

    try {
        const response = await fetch(`http://localhost:8080/qrcode/deletar/${id}`, {
            method: 'DELETE',
            headers: { 'Authorization': 'Basic ' + credencialAdmin }
        });

        // O Spring costuma retornar 204 No Content ou 200 OK para deleções bem-sucedidas
        if (response.ok) {
            alert("🗑️ Registro excluído com sucesso!");
            fetchBrindes(); // Recarrega a tabela imediatamente para sumir com a linha
        } else {
            const errorData = await response.json().catch(() => ({}));
            alert("❌ Erro ao excluir: " + (errorData.mensagemErro || "Falha na operação."));
        }
    } catch (error) {
        alert("❌ Erro de conexão com o servidor ao tentar excluir.");
    }
}

// 5. MÓDULO DA CÂMERA (LEITURA DO QR CODE)
function abrirCamera() {
    if (leitorAtivo) return;

    scannerContainer.style.display = 'block';
    divResultadoScanner.style.display = 'none';

    html5QrcodeScanner = new Html5QrcodeScanner("reader", { fps: 10, qrbox: {width: 250, height: 250} }, false);

    html5QrcodeScanner.render(async (decodedText) => {
        // Sucesso na leitura
        html5QrcodeScanner.clear(); // Para a câmera
        divResultadoScanner.textContent = "Processando QR Code...";
        divResultadoScanner.style.display = "block";
        divResultadoScanner.style.backgroundColor = "#e0f2fe";
        divResultadoScanner.style.color = "#0369a1";

        try {
            const response = await fetch('http://localhost:8080/qrcode/resgatar', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Basic ' + credencialAdmin
                },
                body: JSON.stringify({ token: decodedText })
            });

            if (response.ok) {
                const dados = await response.json();
                divResultadoScanner.innerHTML = `
                                    ✅ <strong>QR Code Validado!</strong><br>
                                    Brinde liberado para: <b>${dados.nome || 'Cliente'}</b><br>
                                    <span style="font-size: 0.85em; color: #047857;">${dados.email}</span>
                                `;
                divResultadoScanner.style.backgroundColor = "#d1fae5";
                divResultadoScanner.style.color = "#065f46";
                fetchBrindes(); // Atualiza a tabela imediatamente
            } else {
                const err = await response.json();
                divResultadoScanner.textContent = "❌ Erro: " + (err.mensagemErro || "QR Code inválido ou já usado.");
                divResultadoScanner.style.backgroundColor = "#fee2e2";
                divResultadoScanner.style.color = "#991b1b";
            }
        } catch (error) {
            divResultadoScanner.textContent = "❌ Falha de conexão com o servidor.";
        }
        leitorAtivo = false;
    }, (error) => {
        // Ignora falhas de foco contínuas
    });

    leitorAtivo = true;
}

function fecharCamera() {
    if (html5QrcodeScanner) {
        html5QrcodeScanner.clear();
    }
    scannerContainer.style.display = 'none';
    leitorAtivo = false;
}

// 6. EVENTOS E INICIALIZAÇÃO DA PÁGINA
searchInput.addEventListener('input', (e) => {
    const filtrados = brindesLista.filter(b => {
            const emailBate = b.email && b.email.toLowerCase().includes(termo);
            const nomeBate = b.nome && b.nome.toLowerCase().includes(termo);
            return emailBate || nomeBate;
        });

        renderTable(filtrados);
});

btnLerQrCode.addEventListener('click', abrirCamera);
btnFecharScanner.addEventListener('click', fecharCamera);

// Ao abrir a página, já pede a senha e carrega os dados
window.onload = fetchBrindes;