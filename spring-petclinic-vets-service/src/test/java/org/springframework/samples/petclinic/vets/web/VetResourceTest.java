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
package org.springframework.samples.petclinic.vets.web;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.vets.model.Specialty;
import org.springframework.samples.petclinic.vets.model.Vet;
import org.springframework.samples.petclinic.vets.model.VetRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static java.util.Arrays.asList;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(VetResource.class)
@ActiveProfiles("test")
class VetResourceTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    VetRepository vetRepository;

    @Test
    void shouldGetAListOfVets() throws Exception {
        Vet vet = new Vet();
        vet.setId(1);

        given(vetRepository.findAll()).willReturn(asList(vet));

        mvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void shouldGetVetWithFullDetails() throws Exception {
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("Anna");
        vet.setLastName("Smith");

        given(vetRepository.findAll()).willReturn(asList(vet));

        mvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].firstName").value("Anna"))
            .andExpect(jsonPath("$[0].lastName").value("Smith"))
            .andExpect(jsonPath("$[0].nrOfSpecialties").value(0));
    }

    @Test
    void shouldGetVetWithSpecialty() throws Exception {
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("Bob");
        vet.setLastName("Jones");

        Specialty specialty = new Specialty();
        specialty.setId(1);
        specialty.setName("radiology");
        vet.addSpecialty(specialty);

        given(vetRepository.findAll()).willReturn(asList(vet));

        mvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].firstName").value("Bob"))
            .andExpect(jsonPath("$[0].lastName").value("Jones"))
            .andExpect(jsonPath("$[0].specialties[0].id").value(1))
            .andExpect(jsonPath("$[0].specialties[0].name").value("radiology"))
            .andExpect(jsonPath("$[0].nrOfSpecialties").value(1));
    }

    @Test
    void shouldGetEmptyVetList() throws Exception {
        given(vetRepository.findAll()).willReturn(new ArrayList<>());

        mvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldGetMultipleVetsWithDifferentDetails() throws Exception {
        Vet vet1 = new Vet();
        vet1.setId(1);
        vet1.setFirstName("Tom");
        vet1.setLastName("Brown");

        Vet vet2 = new Vet();
        vet2.setId(2);
        vet2.setFirstName("Lisa");
        vet2.setLastName("Green");

        Specialty specialty = new Specialty();
        specialty.setId(1);
        specialty.setName("surgery");
        vet2.addSpecialty(specialty);

        given(vetRepository.findAll()).willReturn(asList(vet1, vet2));

        mvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].firstName").value("Tom"))
            .andExpect(jsonPath("$[0].nrOfSpecialties").value(0))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].firstName").value("Lisa"))
            .andExpect(jsonPath("$[1].specialties[0].name").value("surgery"))
            .andExpect(jsonPath("$[1].nrOfSpecialties").value(1));
    }
}
