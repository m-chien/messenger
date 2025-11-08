package com.example.WebCloneMessenger.repos;

import com.example.WebCloneMessenger.Model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AttachmentRepository extends JpaRepository<Attachment, Integer> {

    Attachment findFirstByIdmessageId(Integer id);

}
