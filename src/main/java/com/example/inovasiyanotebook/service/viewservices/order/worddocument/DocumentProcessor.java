package com.example.inovasiyanotebook.service.viewservices.order.worddocument;

import com.vaadin.flow.component.upload.receivers.MemoryBuffer;

public interface DocumentProcessor {
    void processDocument(String fileName, MemoryBuffer buffer);
}

