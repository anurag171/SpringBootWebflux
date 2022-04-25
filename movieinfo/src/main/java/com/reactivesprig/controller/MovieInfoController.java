package com.reactivesprig.controller;

import com.reactivesprig.dto.MovieInfoDto;
import com.reactivesprig.service.MovieInfoService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@RestController
@RequestMapping("v1")
@RequiredArgsConstructor
public class MovieInfoController {

    private final MovieInfoService movieInfoService;

    Sinks.Many<MovieInfoDto> movieInfoSink = Sinks.many().replay().all();

    @PostMapping("/movieinfo")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfoDto> addMovieInfo(@RequestBody @Valid MovieInfoDto movieInfoDto){
      return movieInfoService.addMovie(movieInfoDto).doOnNext(movieInfo -> movieInfoSink.tryEmitNext(movieInfo));
    }

    @GetMapping(value = "/movieinfo/stream",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<MovieInfoDto> getMoviesById(){
        return movieInfoSink.asFlux();
    }


    @GetMapping("/movieinfo")
    @ResponseStatus(HttpStatus.OK)
    public Flux<MovieInfoDto> getAllMovies(@RequestParam(value = "year",required = false) Integer year){
        if(year != null){
            return movieInfoService.getAllMoviesInfoByYear(year)
                .switchIfEmpty(Flux.empty()).log();
        }
        return movieInfoService.getAllMovies().log();
    }

    @GetMapping("/movieinfo/{id}")
    public Mono<ResponseEntity<MovieInfoDto>> getMoviesById(@PathVariable String id){
        return movieInfoService.getMovieById(id)
                                .map(ResponseEntity::ok)
                                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build())).log();
    }

    @PutMapping("/movieinfo/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<MovieInfoDto>> updateMovieInfo(@RequestBody MovieInfoDto movieInfoDto,@PathVariable String id){
        return movieInfoService.updateMovie(movieInfoDto, id)
                                .map(ResponseEntity::ok)
                                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build())).log();
    }

    @DeleteMapping("/movieinfo/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMovieInfo(@PathVariable String id){
        return movieInfoService.deleteMovie(id).log();
    }
}
