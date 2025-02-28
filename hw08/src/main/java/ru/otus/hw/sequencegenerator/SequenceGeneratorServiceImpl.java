package ru.otus.hw.sequencegenerator;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;
import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import org.springframework.data.mongodb.core.query.Update;
import ru.otus.hw.sequencegenerator.sequencemodel.DatabaseSequence;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class SequenceGeneratorServiceImpl implements SequenceGeneratorService {

    private final MongoOperations mongoOperations;


    public String generateSequence(String seqName) {

        var counter = mongoOperations.findAndModify(query(where("_id").is(seqName)),
                new Update().inc("seq",1), options().returnNew(true).upsert(true),
                DatabaseSequence.class);
        return Objects.nonNull(counter) ? String.valueOf(counter.getSeq()) : "1";
    }
}
