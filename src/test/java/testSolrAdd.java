import com.offcn.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @author zjc
 * @create 2019--12--05--17:21
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-solr.xml")
public class testSolrAdd {

    @Autowired
    private SolrTemplate solrTemplate;

    //测试新增数据到搜索引擎
    @Test
    public  void testAddAndEdit() {
        TbItem item = new TbItem();
        //根据id判断是做新增还是修改,有id就是修改,没有id就是增加
        item.setId(1L);

        item.setTitle("测试手机001-update");
        item.setPrice(new BigDecimal(1100.90D));
        item.setSeller("张三");
        item.setGoodsId((10001L));
        item.setUpdateTime(new Date());
        item.setBrand("华为");
        item.setCategory("5G引领");
        item.setImage("1.jpg");

        solrTemplate.saveBean(item);
        //提交事务
        solrTemplate.commit();
    }
    //按照主键查询数据
    @Test
    public void testFindById(){
        TbItem item = solrTemplate.getById(1, TbItem.class);
        System.out.println("id:"+item.getId()+" title:"+item.getTitle()+" price:"+item.getPrice());
    }

    //按照主键删除数据
    @Test
    public void deleteById(){

        solrTemplate.deleteById("1");
        solrTemplate.commit();
    }
    //批量插入100条数据

    @Test
    public void insert(){
        List<TbItem> list =new ArrayList<>();
        for (int i = 0; i <100; i++) {
            TbItem item = new TbItem();
            item.setId(new Long(i));

            item.setTitle("测试手机"+i);
            item.setPrice(new BigDecimal(1200.23D+(i*10)));
            item.setSeller("张三");
            item.setGoodsId((10001L+i));
            item.setUpdateTime(new Date());
            item.setBrand("华为");
            item.setCategory("5G引领");
            item.setImage("1.jpg");
            list.add(item);
        }
        //集中保存集合到Solr
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    //分页查询的测试
    @Test
    public void testPageSearch(){
        //创建查询条件对象 第一个 * 表示要查询的字段 第二个*表示要查询的关键字
        SimpleQuery query = new SimpleQuery("item_title:*");
        //设置游标开始位置
        query.setOffset(0);
        //设置返回的最大记录数
        query.setRows(10);
        //发出查询
        ScoredPage<TbItem> scoredPage = solrTemplate.queryForPage(query, TbItem.class);
        //获取查询结果记录集合
        List<TbItem> list = scoredPage.getContent();
        //获取满足查询条件总记录数
        long totalElements = scoredPage.getTotalElements();
        System.out.println("满足查询条件的总记录数"+totalElements);
        for (TbItem item : list) {
            System.out.println("id:"+item.getId()+"  title:"+item.getTitle()+"  price:"+item.getPrice());
        }
    }

    //分页查询的测试 带查询条件
    @Test
    public void testPageSearch2(){
        //创建查询条件对象 第一个 * 表示要查询的字段 第二个*表示要查询的关键字
        SimpleQuery query = new SimpleQuery();
        //创建查询条件对象
        Criteria criteria = new Criteria("item_title");
        criteria = criteria.contains("9");
        criteria = criteria.and("item_price").greaterThan(1500D);
        //关联查询器对象和查询条件
        query.addCriteria(criteria);
        //创建排序对象
        Sort sort = new Sort(Sort.Direction.DESC, "item_price");
        //关联排序对象到查询器对象
        query.addSort(sort);

        //设置游标开始位置
        query.setOffset(0);
        //设置返回的最大记录数
        query.setRows(10);
        //发出查询
        ScoredPage<TbItem> scoredPage = solrTemplate.queryForPage(query, TbItem.class);
        //获取查询结果记录集合
        List<TbItem> list = scoredPage.getContent();
        //获取满足查询条件总记录数
        long totalElements = scoredPage.getTotalElements();
        System.out.println("满足查询条件的总记录数"+totalElements);
        for (TbItem item : list) {
            System.out.println("id:"+item.getId()+"  title:"+item.getTitle()+"  price:"+item.getPrice());
        }
    }

    //删除全部数据
    @Test
    public void deleteAll(){
        SimpleQuery query = new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }
}
