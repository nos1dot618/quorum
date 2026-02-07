package fun.ninth.quorum.raft.messages;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @Type(value = AppendEntriesRequest.class, name = "AppendEntriesRequest"),
        @Type(value = AppendEntriesResponse.class, name = "AppendEntriesResponse"),
        @Type(value = RequestVoteRequest.class, name = "RequestVoteRequest"),
        @Type(value = RequestVoteResponse.class, name = "RequestVoteResponse"),
})
public sealed interface IRaftMessage permits AppendEntriesRequest, AppendEntriesResponse, RequestVoteRequest, RequestVoteResponse {
}
