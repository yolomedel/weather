function fetchWeather() {
    const token = localStorage.getItem('authToken');
    
    fetch('/weather', {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
        .then(response => {
            if (!response.ok) throw new Error('Network error');
            return response.json();
        })
        .then(data => {
            // Update existing temperature display
            document.getElementById('location').textContent = data.city;
            document.getElementById('temperature').innerHTML = 
                `${data.temperature.toFixed(1)}<span class="unit">${data.tempUnit}</span>`;
            
            // Update new fields
            document.getElementById('humidity').textContent = data.humidity;
            document.getElementById('windSpeed').textContent = data.windSpeed.toFixed(1);
            
            // Update icon based on temperature
            updateWeatherIcon(data.temperature);
        })
        .catch(error => {
            console.error('Error:', error);
            document.getElementById('errorMessage').textContent = 'Error loading weather data';
        });
}

function updateWeatherIcon(temp) {
    const icon = document.getElementById('weatherIcon');
    if(temp > 25) {
        icon.textContent = '☀️';
    } else if(temp > 15) {
        icon.textContent = '⛅';
    } else {
        icon.textContent = '❄️';
    }
}

function changeCity() {
    const cityInput = document.getElementById('cityInput');
    const city = cityInput.value.trim();
    
    if (!city) {
        document.getElementById('errorMessage').textContent = 'Please enter a city name';
        return;
    }
    
    document.getElementById('errorMessage').textContent = '';
    
    fetch('/weather/city', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ city: city })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('City not found');
        }
        return response.text();
    })
    .then(() => {
        fetchWeather(); // Refresh weather after city change
    })
    .catch(error => {
        console.error('Error:', error);
        document.getElementById('errorMessage').textContent = 'City not found or service unavailable';
    });
}

// Load weather when page loads
document.addEventListener('DOMContentLoaded', fetchWeather);

// Allow pressing Enter in search input
document.getElementById('cityInput').addEventListener('keypress', function(e) {
    if (e.key === 'Enter') {
        changeCity();
    }
});

