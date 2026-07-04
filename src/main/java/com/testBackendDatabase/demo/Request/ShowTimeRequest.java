package com.testBackendDatabase.demo.Request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShowTimeRequest
{
@NotNull(message = "showtime id null")
 private Long id;
}