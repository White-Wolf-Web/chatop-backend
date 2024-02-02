package com.chatop.backend.controller;

import com.chatop.backend.dto.RentalDto;
import com.chatop.backend.dto.RentalRequestDTO;
import com.chatop.backend.model.Rental;
import com.chatop.backend.service.RentalService;
import com.chatop.backend.service.RentalServiceImpl;
import com.chatop.backend.service.UploadFileService;
import com.chatop.backend.exception.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
//import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;
import java.util.NoSuchElementException;

import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RentalController {

    private final RentalService rentalService;
    private final UploadFileService fileUpload;
    private final Logger logger = LoggerFactory.getLogger(RentalController.class);


    @Autowired
    public RentalController(RentalService rentalService, UploadFileService fileUpload) {
        this.rentalService = rentalService;
        this.fileUpload = fileUpload;
    }

    @Operation(summary = "Récupération de toutes les locations", description = "Retourne une liste de toutes les locations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des locations retournée avec succès"),
            @ApiResponse(responseCode = "400", description = "Probleme dans la requete"),
            @ApiResponse(responseCode = "404", description = "Aucune 'location' retrouvée")
    })
    @GetMapping
    public ResponseEntity<List<RentalDto>> getAllRentals() {
        return ResponseEntity.ok(rentalService.getAllRentals());
    }

    @Operation(summary = "Récupération d'une location par son ID", description = "Retourne les détails d'une location spécifique grâce à son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Location trouvée et retournée avec succès"),
            @ApiResponse(responseCode = "400", description = "Problème avec l'Id fourni"),
            @ApiResponse(responseCode = "404", description = "Location non trouvée")
    })
    @GetMapping("/rentals/{id}")
    public ResponseEntity<Rental> getRentalById(@PathVariable Long id) {
        if (id == null) {
            return ResponseEntity.badRequest().body(null);
        }
        Rental rental = rentalService.getRentalById(id);
        if (rental == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(rental);
    }

    @Operation(summary = "Création d'une nouvelle location", description = "Création d'une nouvelle location et la retourne")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Nouvelle location créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Probleme dans la requete lors de la création d'une nouvelle location"),
    })
    @PostMapping("/rentals")
    public ResponseEntity<?> createRental(
            @Valid @RequestParam("name") String name,
            @Valid @RequestParam("surface") int surface,
            @Valid @RequestParam("price") int price,
            @Valid @RequestParam("picture") MultipartFile picture,
            @Valid @RequestParam("description") String description,
            @Valid Long owner_id) throws IOException {
        if (picture.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Le fichier est vide encore !.");
        }
        try {
            String pictureUrl = fileUpload.uploadFile(picture);
            RentalDto rentalDto = rentalService.createRental(name, surface, price, pictureUrl, description, owner_id);
            return new ResponseEntity<>(rentalDto, HttpStatus.CREATED);
        }  catch (EntityNotFoundException ex) {
            logger.error("Entité non trouvée", ex);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé avec l'ID: " + owner_id);
        }
        catch (IOException ex) {
            logger.error("Une erreur s'est produite lors du téléchargement de l'image", ex);
            ErrorResponse errorResponse = new ErrorResponse("Erreur de téléchargement de fichier",
                    Collections.singletonList("Une erreur s'est produite lors du téléchargement de l'image"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        } catch (Exception e) {
            logger.error("Erreur lors de la création de la location", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la création de la location");
        }
    }

    @Operation(summary = "Mettre à jour d'une location", description = "Mise à jour les détails d'une location existante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Succès de la mise à jour de 'Location'"),
            @ApiResponse(responseCode = "404", description = "Mise à jour de 'Location' non trouvée")
    })
    @PutMapping("/rentals/{id}")
    public ResponseEntity<RentalDto> updateRental(@PathVariable Long id, @RequestBody RentalRequestDTO rentalRequestDTO) {
        try {
            return ResponseEntity.ok(rentalService.updateRental(id, rentalRequestDTO));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}

