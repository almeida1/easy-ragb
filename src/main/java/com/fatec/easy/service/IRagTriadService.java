package com.fatec.easy.service;

import com.fatec.easy.model.RagAnswerResponse;

public interface IRagTriadService {
    RagAnswerResponse answer(String question);
}
