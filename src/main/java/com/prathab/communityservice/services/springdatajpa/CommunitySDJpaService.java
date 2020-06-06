/*
 * Copyright 2020 Prathab Murugan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.prathab.communityservice.services.springdatajpa;

import com.prathab.communityservice.domain.Community;
import com.prathab.communityservice.domain.CommunityAdmin;
import com.prathab.communityservice.dto.CommunityDto;
import com.prathab.communityservice.dto.mapper.CommunityMapper;
import com.prathab.communityservice.repositories.CommunityAdminRepository;
import com.prathab.communityservice.repositories.CommunityRepository;
import com.prathab.communityservice.services.CommunityService;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CommunitySDJpaService implements CommunityService {
  private final CommunityRepository communityRepository;
  private final CommunityAdminRepository communityAdminRepository;
  private final CommunityMapper communityMapper;

  public CommunitySDJpaService(
      CommunityRepository communityRepository,
      CommunityAdminRepository communityAdminRepository,
      CommunityMapper communityMapper) {
    this.communityRepository = communityRepository;
    this.communityAdminRepository = communityAdminRepository;
    this.communityMapper = communityMapper;
  }

  @Override public Community createCommunity(CommunityDto communityDto) {
    communityDto.setCommunityId(generateUniqueCommunityId());
    var community = communityMapper.communityDtoToCommunity(communityDto);
    var savedCommunity = communityRepository.save(community);
    log.trace("saved community with id[{}] to repository", savedCommunity.getId());
    return savedCommunity;
  }

  @Override public Set<Community> listAll() {
    var communityListSet = new HashSet<Community>();
    communityRepository.findAll().forEach(communityListSet::add);
    return communityListSet;
  }

  @Override public Community getCommunityDetailsById(String communityId) {
    return communityRepository.findByCommunityId(communityId);
  }

  @Override public Community addAdminsToCommunity(String communityId, Set<String> admins) {
    var community = communityRepository.findByCommunityId(communityId);

    var savedAdminSet = new HashSet<CommunityAdmin>();
    admins.forEach(s -> {
      var admin = new CommunityAdmin();
      admin.setAdminId(s);
      admin.getCommunities().add(community);
      savedAdminSet.add(communityAdminRepository.save(admin));
    });

    community.getAdmins().addAll(savedAdminSet);
    return communityRepository.save(community);
  }

  private String generateUniqueCommunityId() {
    return UUID.randomUUID().toString();
  }
}