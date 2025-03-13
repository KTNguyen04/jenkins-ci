/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.vets.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VetTest {

    private Vet vet;
    private Specialty specialty1;
    private Specialty specialty2;

    @BeforeEach
    void setUp() {
        vet = new Vet();
        specialty1 = new Specialty();
        specialty1.setId(1);
        specialty1.setName("Surgery");

        specialty2 = new Specialty();
        specialty2.setId(2);
        specialty2.setName("Dentistry");
    }

    @Test
    void testGettersAndSetters() {
        // Test ID
        vet.setId(1);
        assertEquals(1, vet.getId());

        // Test First Name
        vet.setFirstName("John");
        assertEquals("John", vet.getFirstName());

        // Test Last Name
        vet.setLastName("Doe");
        assertEquals("Doe", vet.getLastName());
    }

    @Test
    void testSpecialtiesInitialization() {
        // Test that specialties is initialized when null
        assertNotNull(vet.getSpecialtiesInternal());
        assertTrue(vet.getSpecialtiesInternal().isEmpty());
    }

    @Test
    void testAddSpecialty() {
        // Test adding a single specialty
        vet.addSpecialty(specialty1);
        assertEquals(1, vet.getNrOfSpecialties());
        assertTrue(vet.getSpecialtiesInternal().contains(specialty1));
    }

    @Test
    void testGetSpecialtiesSorted() {
        // Test that specialties are returned sorted by name
        vet.addSpecialty(specialty2); // Dentistry
        vet.addSpecialty(specialty1); // Surgery
        
        List<Specialty> specialties = vet.getSpecialties();
        assertEquals(2, specialties.size());
        assertEquals("Dentistry", specialties.get(0).getName()); // Should come first alphabetically
        assertEquals("Surgery", specialties.get(1).getName());
        
        // Test that returned list is unmodifiable
        assertThrows(UnsupportedOperationException.class, () -> {
            specialties.add(new Specialty());
        });
    }

    @Test
    void testGetNrOfSpecialties() {
        assertEquals(0, vet.getNrOfSpecialties());
        
        vet.addSpecialty(specialty1);
        assertEquals(1, vet.getNrOfSpecialties());
        
        vet.addSpecialty(specialty2);
        assertEquals(2, vet.getNrOfSpecialties());
    }

    @Test
    void testSpecialtiesInternalNotNull() {
        // Test that getSpecialtiesInternal always returns non-null
        Vet newVet = new Vet();
        assertNotNull(newVet.getSpecialtiesInternal());
        
        // Even after adding specialties
        newVet.addSpecialty(specialty1);
        assertNotNull(newVet.getSpecialtiesInternal());
    }
}
