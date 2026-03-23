// Configuração da API do Spring Boot
const API_CONFIG = {
    // Rota pública liberada no SecurityConfig
    url: 'http://localhost:8080/qrcode/gerar',
    headers: {
        'Content-Type': 'application/json'
    }
};

// Elementos do DOM
const form = document.getElementById('clientForm');
const nomeInput = document.getElementById('nome');
const emailInput = document.getElementById('email');
const submitBtn = document.getElementById('submitBtn');
const spinner = document.getElementById('spinner');
const btnText = document.querySelector('.btn-text');
const successAlert = document.getElementById('successAlert');
const errorAlert = document.getElementById('errorAlert');
const successMessage = document.getElementById('successMessage');
const errorMessage = document.getElementById('errorMessage');
const nomeError = document.getElementById('nomeError');
const emailError = document.getElementById('emailError');

// Funções de validação
function isValidEmail(email) {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
}

function isValidName(name) {
    return name.trim().length >= 3;
}

function showFieldError(input, errorElement, message) {
    input.classList.add('error');
    errorElement.textContent = message;
}

function clearFieldError(input, errorElement) {
    input.classList.remove('error');
    errorElement.textContent = '';
}

function validateForm() {
    let isValid = true;

    if (!isValidName(nomeInput.value)) {
        showFieldError(nomeInput, nomeError, 'O nome deve ter pelo menos 3 caracteres');
        isValid = false;
    } else {
        clearFieldError(nomeInput, nomeError);
    }

    if (!isValidEmail(emailInput.value)) {
        showFieldError(emailInput, emailError, 'Digite um e-mail válido');
        isValid = false;
    } else {
        clearFieldError(emailInput, emailError);
    }

    return isValid;
}

function showSuccessAlert(message) {
    successMessage.textContent = message;
    successAlert.style.display = 'flex';
    errorAlert.style.display = 'none';

    // Esconde a mensagem depois de 8 segundos
    setTimeout(() => { successAlert.style.display = 'none'; }, 8000);
}

function showErrorAlert(message) {
    errorMessage.textContent = message;
    errorAlert.style.display = 'flex';
    successAlert.style.display = 'none';

    setTimeout(() => { errorAlert.style.display = 'none'; }, 8000);
}

// 🚀 Função Real de Envio para a API Java
async function submitToAPI(data) {
    try {
        const response = await fetch(API_CONFIG.url, {
            method: 'POST',
            headers: API_CONFIG.headers,
            body: JSON.stringify(data) // Manda {nome: "...", email: "..."}
        });

        if (response.ok) {
            return { success: true };
        } else {
            // Se o Java retornar um 400 Bad Request, tenta ler a mensagem da RegraNegocioException
            const errorData = await response.json().catch(() => ({}));
            return { success: false, error: errorData.mensagemErro || `Erro do servidor: ${response.status}` };
        }
    } catch (error) {
        console.error('Erro de conexão:', error);
        return { success: false, error: 'Servidor offline ou erro de rede (CORS).' };
    }
}

// Handler do submit
async function handleSubmit(e) {
    e.preventDefault();

    if (!validateForm()) return;

    // Estado de Loading
    submitBtn.disabled = true;
    btnText.style.display = 'none';
    spinner.style.display = 'inline-block';

    const formData = {
        nome: nomeInput.value.trim(),
        email: emailInput.value.trim(),
    };

    // Bate na API do Spring Boot
    const result = await submitToAPI(formData);

    // Remove estado de Loading
    submitBtn.disabled = false;
    btnText.style.display = 'inline';
    spinner.style.display = 'none';

    if (result.success) {
        showSuccessAlert('QR Code gerado com sucesso! Verifique sua caixa de entrada e spam.');
        form.reset(); // Limpa o formulário
    } else {
        showErrorAlert(result.error || 'Não foi possível gerar o QR Code no momento.');
    }
}

// Event Listeners
form.addEventListener('submit', handleSubmit);

// Validação visual em tempo real
nomeInput.addEventListener('input', () => {
    if (nomeError.textContent && isValidName(nomeInput.value)) clearFieldError(nomeInput, nomeError);
});

emailInput.addEventListener('input', () => {
    if (emailError.textContent && isValidEmail(emailInput.value)) clearFieldError(emailInput, emailError);
});

nomeInput.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') {
        e.preventDefault();
        emailInput.focus();
    }
});