import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CanchaForm } from './cancha-form';

describe('CanchaForm', () => {
  let component: CanchaForm;
  let fixture: ComponentFixture<CanchaForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CanchaForm],
    }).compileComponents();

    fixture = TestBed.createComponent(CanchaForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
