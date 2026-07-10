package com.testBackendDatabase.demo.Chatbot.Tools;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.testBackendDatabase.demo.Chatbot.ChatbotTool;
import com.testBackendDatabase.demo.Repository.MovieInfoRepository;
import com.testBackendDatabase.demo.model.MovieInfo;

import java.util.Map;

@Component
public class TopMovieTool implements ChatbotTool {

    private final MovieInfoRepository movieInfoRepository;

    public TopMovieTool(MovieInfoRepository movieInfoRepository

    )
    {
        this.movieInfoRepository=movieInfoRepository;
    }
    

    @Override
    public String getToolName() {
        return "get_top_selling_movie";
    }

    @Override
    public Map<String, Object> getToolDefinition() {
        return Map.of(
            "type", "function",
            "function", Map.of(
                "name", getToolName(),
                "description", "Get the top best-selling movie with the highest number of tickets sold from the database.",
                "parameters", Map.of(
                    "type", "object",
                    "properties", Map.of() // Không cần tham số đầu vào vì chỉ lấy top 1 phim hot nhất
                )
            )
        );
    }

    @Override
    public String execute(String argumentsJson) {
        try{
        System.out.println("==> Khởi chạy Code Java Local [TopMovieTool] truy vấn Database...");
        MovieInfo result=movieInfoRepository.findTopBookedMovie().orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Không tìm thấy dữ liệu"));
        return result.toString();
        }
        catch(Exception e)
        {
          return "{\"status\": \"error\", \"message\": \"Không thể truy cập cơ sở dữ liệu tại thời điểm này.\"}";
        }     
       
    }
}