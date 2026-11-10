import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CanchaList } from './cancha-list';

describe('CanchaList', () => {
  let component: CanchaList;
  let fixture: ComponentFixture<CanchaList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CanchaList],
    }).compileComponents();

    fixture = TestBed.createComponent(CanchaList);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
