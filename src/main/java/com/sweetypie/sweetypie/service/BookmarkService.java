package com.sweetypie.sweetypie.service;

import com.sweetypie.sweetypie.dto.BookmarkDto;
import com.sweetypie.sweetypie.exception.DataNotFoundException;
import com.sweetypie.sweetypie.exception.DuplicateValueExeption;
import com.sweetypie.sweetypie.model.Accommodation;
import com.sweetypie.sweetypie.model.Bookmark;
import com.sweetypie.sweetypie.model.Member;
import com.sweetypie.sweetypie.repository.AccommodationRepository;
import com.sweetypie.sweetypie.repository.BookmarkRepository;
import com.sweetypie.sweetypie.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;

    private final MemberRepository memberRepository;

    private final AccommodationRepository accommodationRepository;

    public List<BookmarkDto> findBookmarksByMemberId(long memberId) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new DataNotFoundException("Not Found Member"));

        List<Bookmark> bookmarks = bookmarkRepository.findBookmarksByMemberId(memberId);

        List<BookmarkDto> bookmarkDtos = new ArrayList<>();

        for (Bookmark bookmark : bookmarks) {
            bookmarkDtos.add(new BookmarkDto(bookmark.getId(), bookmark.getMember().getId(),
                    bookmark.getAccommodation().getId()));
        }

        return bookmarkDtos;
    }

    public List<BookmarkDto> findBookmarksByMemberEmail(String email) {

        List<Bookmark> bookmarks = bookmarkRepository.findBookmarksByMemberEmail(email);

        List<BookmarkDto> bookmarkDtos = new ArrayList<>();

        for (Bookmark bookmark : bookmarks) {
            bookmarkDtos.add(new BookmarkDto(bookmark.getId(), bookmark.getMember().getId(),
                    bookmark.getAccommodation().getId()));
        }

        return bookmarkDtos;
    }

    public void postBookmark(BookmarkDto bookmarkDto) {
        Member member = memberRepository.findById(bookmarkDto.getMemberId())
                .orElseThrow(() -> new DataNotFoundException("Member Not Found"));

        Accommodation accommodation = accommodationRepository.findById(bookmarkDto.getAccommodationId())
                .orElseThrow(() -> new DataNotFoundException("Accommodation Not Found"));

        if (bookmarkRepository.findBookmarkByMemberIdAndAccommodationId(bookmarkDto.getMemberId(),
                bookmarkDto.getAccommodationId()).isPresent()) {
            throw new DuplicateValueExeption("Already Have a Bookmark");
        }

        Bookmark bookmark = new Bookmark();
        bookmark.setMember(member);
        bookmark.setAccommodation(accommodation);

        bookmarkRepository.save(bookmark);
    }

    public void deleteBookmark(long memberId, long accommodationId) {
        Bookmark bookmark = bookmarkRepository.findBookmarkByMemberIdAndAccommodationId(memberId, accommodationId)
                .orElseThrow(() -> new DataNotFoundException("Bookmark Not Found"));

        bookmarkRepository.delete(bookmark);
    }
}