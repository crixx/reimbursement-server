package ch.uzh.csg.reimbursement.rest;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.csg.reimbursement.dto.AccountDto;
import ch.uzh.csg.reimbursement.dto.CostCategoryDto;
import ch.uzh.csg.reimbursement.model.User;
import ch.uzh.csg.reimbursement.service.AccountService;
import ch.uzh.csg.reimbursement.service.CostCategoryService;
import ch.uzh.csg.reimbursement.service.ExpenseService;
import ch.uzh.csg.reimbursement.service.UserService;
import ch.uzh.csg.reimbursement.view.ExpenseResourceView;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/prof")
@PreAuthorize("hasRole('PROF')")
@Api(value = "Professor", description = "Authorized access required, only for professors")
public class ProfessorResource {

	// resource naming convention
	// http://www.restapitutorial.com/lessons/restfulresourcenaming.html

	@Autowired
	private UserService userService;

	@Autowired
	private ExpenseService expenseService;

	@Autowired
	private CostCategoryService costCategoryService;

	@Autowired
	private AccountService accountService;

	@RequestMapping(value = "/users", method = GET)
	@ApiOperation(value = "Find all users", notes = "Finds all users which are currently in the system.")
	public List<User> getAllUsers() {
		return userService.findAll();
	}

	@RequestMapping(value = "/users/{user-uid}", method = GET)
	@ApiOperation(value = "Find one user with an uid", notes = "Finds exactly one user by its uid.")
	public User findUserByUid(@PathVariable("user-uid") String uid) {
		return userService.findByUid(uid);
	}

	@RequestMapping(value = "/expenses/user/{user-uid}", method = GET)
	@ApiOperation(value = "Find all expenses for a given user", notes = "Finds all expenses that were created by the user.")
	public ExpenseResourceView getAllExpenses(@PathVariable ("user-uid") String uid) {
		return expenseService.findAllByUser(uid);
	}

	@RequestMapping(value = "/costCategories", method = POST)
	@ApiOperation(value = "Create a new costCategory")
	@ResponseStatus(CREATED)
	public void createCostCategory(@RequestBody CostCategoryDto dto) {
		costCategoryService.create(dto);
	}

	@RequestMapping(value = "/accounts", method = POST)
	@ApiOperation(value = "Create a new account")
	@ResponseStatus(CREATED)
	public void createAccount(@RequestBody AccountDto dto) {
		accountService.create(dto);
	}
}