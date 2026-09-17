package co.uniajc.agrovalle.agrovalleconnect.controllers;

import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;

import co.uniajc.agrovalle.agrovalleconnect.services.LoteCosechaService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController

@RequestMapping("/api/v1/productos")

public class LoteCosechaController {

    @Autowired

    private LoteCosechaService loteCosechaService;

    @PostMapping

    public ResponseEntity<?> publicarLote(

            @RequestParam Long agricultorId,

            @Valid @RequestBody LoteCosecha lote) {

        try {

            LoteCosecha nuevoLote = loteCosechaService.publicarLote(agricultorId, lote);

            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoLote);

        } catch (IllegalArgumentException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        }

    }

    @GetMapping

    public ResponseEntity<List<LoteCosecha>> listarLotes() {

        return ResponseEntity.ok(loteCosechaService.listarLotesActivos());

    }

    @GetMapping("/agricultor/{agricultorId}")

    public ResponseEntity<List<LoteCosecha>> listarPorAgricultor(@PathVariable Long agricultorId) {

        return ResponseEntity.ok(loteCosechaService.listarLotesPorAgricultor(agricultorId));

    }

}
 