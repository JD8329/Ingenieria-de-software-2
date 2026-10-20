package co.uniajc.agrovalle.agrovalleconnect.dto;

public record TokenResponse(String accessToken, String tokenType, long expiresIn) {
}
