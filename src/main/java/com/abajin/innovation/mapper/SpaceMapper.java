package com.abajin.innovation.mapper;

import com.abajin.innovation.entity.Space;
import jakarta.validation.Valid;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 空间Mapper接口
 */
@Mapper
public interface SpaceMapper {
    /**
     * 根据ID查询空间
     */
    Space selectById(@Param("id") Long id);

    /**
     * 查询所有空间
     */
    List<Space> selectAll();

    /**
     * 根据类型查询空间
     */
    List<Space> selectByTypeId(@Param("spaceTypeId") Long spaceTypeId);

    /**
     * 根据状态查询空间
     */
    List<Space> selectByStatus(@Param("status") String status);

    /**
     * 插入空间
     */
    int insert(Space space);

    /**
     * 更新空间
     */
    int update(Space space);

    /**
     * 删除空间
     */
    int deleteById(@Param("id") Long id);

    /**
     *插入空间
     */
    int insertSpace(@Valid Space space);


    /**
     *根据名字查询条数
     */
    int selectCountByName(String name);
}
