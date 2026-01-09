package com.example.WebCloneMessenger.repos;

import com.example.WebCloneMessenger.Model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface AttachmentRepository extends JpaRepository<Attachment, Integer> {

    Attachment findFirstByIdmessageId(Integer id);

    List<Attachment> findByIdmessage_IdIn(List<Integer> messageIds);

}
